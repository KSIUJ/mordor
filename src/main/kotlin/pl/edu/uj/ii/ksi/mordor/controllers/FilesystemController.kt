package pl.edu.uj.ii.ksi.mordor.controllers

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.exceptions.NotFoundException
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryFile
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Controller
class FilesystemController(val repoService: RepositoryService) {
    data class FileEntry(
        val path: String,
        val name: String,
        val iconName: String
    )

    @GetMapping("/file/**")
    fun fileIndex(request: HttpServletRequest): ModelAndView {
        val entity = repoService.getEntity(request.servletPath) ?: throw NotFoundException()

        if (entity is RepositoryDirectory) {
            if (!request.servletPath.endsWith("/")) {
                return ModelAndView("redirect:" + request.servletPath + "/")
            }

            val sortedChildren = entity.getChildren().sortedWith(compareBy({ it !is RepositoryDirectory }, { it.name }))
                    .map { ch -> FileEntry(ch.relativePath, ch.name, getIconName(ch)) }

            return ModelAndView("tree",
                    mapOf("children" to sortedChildren,
                            "path" to entity.relativePath))
        } else {
            return ModelAndView("redirect:/download/" + entity.relativePath)
        }
    }

    @GetMapping("/download/**")
    fun download(request: HttpServletRequest, response: HttpServletResponse) {
        val entity = (repoService.getEntity(request.servletPath)
                ?: throw NotFoundException()) as? RepositoryFile
                ?: throw BadRequestException("not a file")

        response.addHeader("X-Content-Type-Options", "nosniff")
        response.contentType = getMimeForPath(entity.relativePath)

        val stream = entity.newInputStream()
        stream.use {
            IOUtils.copy(stream, response.outputStream)
        }
        response.flushBuffer()
    }

    // TODO: move to a service
    fun getIconName(entity: RepositoryEntity): String {
        if (entity is RepositoryDirectory) {
            return "folder"
        }

        val exts = mapOf(
                ".pdf" to "file-pdf"
        )

        for ((ext, icon) in exts) {
            if (entity.name.toLowerCase().endsWith(ext)) {
                return icon
            }
        }

        return "file"
    }

    // TODO: move to a service
    fun getMimeForPath(path: String): String {
        // careful about XSS!
        val exts = mapOf(
                ".pdf" to "application/pdf",
                ".png" to "image/png",
                ".jpg" to "image/jpeg",
                ".jpeg" to "image/jpeg"
        )

        for ((ext, mime) in exts) {
            if (path.toLowerCase().endsWith(ext)) {
                return mime
            }
        }

        return "application/octet-stream"
    }
}
