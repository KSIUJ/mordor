package pl.edu.uj.ii.ksi.mordor.controllers

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.exceptions.NotFoundException
import pl.edu.uj.ii.ksi.mordor.services.IconNameProvider
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryFile
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Controller
class FilesystemController(
    private val repoService: RepositoryService,
    private val iconNameProvider: IconNameProvider
) {
    data class FileEntry(
        val path: String,
        val name: String,
        val iconName: String
    )

    private data class RelativeDir(
        val name: String,
        var path: String
    )

    private fun createBreadcrumb(entity: RepositoryEntity): List<RelativeDir> {
        val pathBreadcrumb = mutableListOf(RelativeDir("Home", "/file/"))
        var prev = "/file/"
        entity.relativePath.split('/').forEach { dir ->
            pathBreadcrumb.add(RelativeDir(dir, prev + dir))
            prev = "$prev$dir/"
        }
        return pathBreadcrumb
    }

    @GetMapping("/file/**")
    fun fileIndex(request: HttpServletRequest): ModelAndView {
        val entity = repoService.getEntity(request.servletPath.removePrefix("/file/")) ?: throw NotFoundException()

        if (entity is RepositoryDirectory) {
            if (!request.servletPath.endsWith("/")) {
                return ModelAndView("redirect:" + request.servletPath + "/")
            }

            val sortedChildren = entity.getChildren()
                .sortedWith(compareBy({ it !is RepositoryDirectory }, { it.name }))
                .map { entry ->
                    FileEntry(entry.relativePath +
                        if (entry is RepositoryDirectory) "/" else "", entry.name, iconNameProvider.getIconName(entry))
                }

            return ModelAndView("tree", mapOf("children" to sortedChildren, "path" to createBreadcrumb(entity)))
        } else if (entity is RepositoryFile) {
            if (entity.mimeType.startsWith("text/") || entity.isCode) {
                val text = FileUtils.readFileToString(entity.file, "utf-8")
                // TODO: detect encoding
                return ModelAndView("preview", mapOf(
                    "text" to text,
                    "path" to createBreadcrumb(entity),
                    "download" to "/download/${entity.relativePath}"
                ))
            }
        }
        return ModelAndView("redirect:/download/${entity.relativePath}")
    }

    @GetMapping("/download/**")
    fun download(request: HttpServletRequest, response: HttpServletResponse) {
        val entity = (repoService.getEntity(request.servletPath.removePrefix("/download/"))
            ?: throw NotFoundException()) as? RepositoryFile
            ?: throw BadRequestException("not a file")

        response.addHeader("X-Content-Type-Options", "nosniff")
        response.contentType = entity.browserSafeMimeType

        val stream = entity.file.inputStream()
        stream.use {
            IOUtils.copy(stream, response.outputStream)
        }
        response.flushBuffer()
    }
}
