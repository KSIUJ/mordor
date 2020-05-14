package pl.edu.uj.ii.ksi.mordor.controllers

import java.io.ByteArrayInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriUtils
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.exceptions.NotFoundException
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.services.IconNameProvider
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryFile
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Controller
class FilesystemController(
    private val repoService: RepositoryService,
    private val iconNameProvider: IconNameProvider,
    private val entryRepository: FileEntryRepository,
    @Value("\${mordor.preview.max_text_bytes:1048576}") private val maxTextBytes: Int,
    @Value("\${mordor.preview.max_image_bytes:10485760}") private val maxImageBytes: Int,
    @Value("\${mordor.list_hidden_files:false}") private val listHiddenFiles: Boolean
) {
    data class FileEntry(
        val path: String,
        val name: String,
        val iconName: String,
        val relativePath: String,
        val thumbnailPath: String?
    )

    private data class RelativeDir(
        val name: String,
        var path: String
    )

    private enum class FileType(val previewType: String) {
        IMAGE("IMAGE"),
        PAGE("PAGE"),
        TOO_LARGE("TOO_LARGE"),
        CODE("CODE")
    }

    private fun createBreadcrumb(entity: RepositoryEntity): List<RelativeDir> {
        val pathBreadcrumb = mutableListOf(RelativeDir("Home", "/file/"))
        var prev = "/file/"
        entity.relativePath.split('/').forEach { dir ->
            pathBreadcrumb.add(RelativeDir(dir, prev + dir))
            prev = "$prev$dir/"
        }
        return pathBreadcrumb
    }

    private fun createTitle(path: String): String {
        return path.trim('/').replace("/", " / ") + (if (path.isNotEmpty()) " - " else "") + "Mordor"
    }

    private fun urlEncodePath(path: String): String {
        return UriUtils.encodePath(path, "UTF-8")
    }

    private fun previewText(entity: RepositoryFile, path: String): ModelAndView {
        if (entity.file.length() > maxTextBytes) {
            return ModelAndView("preview/preview", mapOf(
                "title" to createTitle(path),
                "path" to createBreadcrumb(entity),
                "download" to "/download/${entity.relativePath}",
                "type" to FileType.TOO_LARGE.previewType
            ))
        }
        val text = FileUtils.readFileToString(entity.file, "utf-8")
        // TODO: detect encoding
        return ModelAndView("preview/preview", mapOf(
            "title" to createTitle(path),
            "text" to text,
            "path" to createBreadcrumb(entity),
            "download" to "/download/${entity.relativePath}",
            "type" to FileType.CODE.previewType
        ))
    }

    private fun previewImage(entity: RepositoryFile, path: String): ModelAndView {
        if (entity.file.length() > maxImageBytes) {
            return ModelAndView("preview/preview", mapOf(
                "title" to createTitle(path),
                "path" to createBreadcrumb(entity),
                "download" to "/download/${entity.relativePath}",
                "type" to FileType.TOO_LARGE.previewType
            ))
        }
        return ModelAndView("preview/preview", mapOf(
            "title" to createTitle(path),
            "path" to createBreadcrumb(entity),
            "download" to "/download/${entity.relativePath}",
            "type" to FileType.IMAGE.previewType
        ))
    }

    private fun previewPage(entity: RepositoryFile, path: String): ModelAndView {
        return ModelAndView("preview/preview", mapOf(
            "title" to createTitle(path),
            "raw" to "/raw/$path",
            "path" to createBreadcrumb(entity),
            "download" to "/download/${entity.relativePath}",
            "type" to FileType.PAGE.previewType
        ))
    }

    @Secured(Permission.READ_STR)
    @GetMapping("/file/**")
    fun fileIndex(request: HttpServletRequest): ModelAndView {
        val path = request.servletPath.removePrefix("/file/")
        val entity = repoService.getEntity(path) ?: throw NotFoundException(path)

        if (entity is RepositoryDirectory) {
            if (!request.servletPath.endsWith("/")) {
                return ModelAndView(RedirectView(urlEncodePath(request.servletPath + "/")))
            }
            val canListHidden = listHiddenFiles || SecurityContextHolder.getContext().authentication.authorities
                .contains(Permission.ROLE_LIST_HIDDEN_FILES)
            val sortedChildren = entity.getChildren(canListHidden)
                .sortedWith(compareBy({ it !is RepositoryDirectory }, { it.name }))
                .map { entry ->
                    FileEntry(entry.relativePath + if (entry is RepositoryDirectory) "/" else "",
                            entry.name,
                            iconNameProvider.getIconName(entry),
                            entity.relativePath + entry.relativePath,
                            if (entry is RepositoryFile && entry.thumbnail != null) entry.thumbnail else null)
                }

            return ModelAndView("tree", mapOf(
                "title" to createTitle(path),
                "children" to sortedChildren,
                "path" to createBreadcrumb(entity)))
        } else if (entity is RepositoryFile) {
            when {
                entity.isPage -> return previewPage(entity, path)
                entity.mimeType.startsWith("text/") || entity.isCode -> return previewText(entity, path)
                entity.isDisplayableImage -> return previewImage(entity, path)
            }
        }
        return ModelAndView(RedirectView(urlEncodePath("/download/${entity.relativePath}")))
    }

    @Secured(Permission.READ_STR)
    @GetMapping("/thumbnail/**")
    fun fileThumbnail(request: HttpServletRequest, response: HttpServletResponse) {
        val path = request.servletPath.removePrefix("/thumbnail")
        val entity = (repoService.getEntity(path)
                ?: throw NotFoundException(path)) as? RepositoryFile
                ?: throw BadRequestException("not a file")

        val thumbnail = entryRepository.findById(path).get().metadata?.thumbnail?.thumbnail
        response.contentType = entity.browserSafeMimeType
        thumbnail?.size?.toLong()?.let { response.setContentLengthLong(it) }

        val stream = ByteArrayInputStream(thumbnail)
        stream.use {
            IOUtils.copy(stream, response.outputStream)
        }
        response.flushBuffer()
    }

    @Secured(Permission.READ_STR)
    @GetMapping("/download/**")
    fun download(request: HttpServletRequest, response: HttpServletResponse) {
        val path = request.servletPath.removePrefix("/download/")
        val entity = (repoService.getEntity(path)
            ?: throw NotFoundException(path)) as? RepositoryFile
            ?: throw BadRequestException("not a file")

        response.addHeader("X-Content-Type-Options", "nosniff")
        response.contentType = entity.browserSafeMimeType
        response.setContentLengthLong(entity.file.length())

        val stream = entity.file.inputStream()
        stream.use {
            IOUtils.copy(stream, response.outputStream)
        }
        response.flushBuffer()
    }

    @Secured(Permission.READ_STR)
    @GetMapping("/raw/**")
    fun raw(request: HttpServletRequest, response: HttpServletResponse) {
        val path = request.servletPath.removePrefix("/raw/")
        val entity = (repoService.getEntity(path)
            ?: throw NotFoundException(path)) as? RepositoryFile
            ?: throw BadRequestException("not a file")

        response.contentType = entity.mimeType
        response.setContentLengthLong(entity.file.length())

        val stream = entity.file.inputStream()
        stream.use {
            IOUtils.copy(stream, response.outputStream)
        }
        response.flushBuffer()
    }

    @ExceptionHandler(value = [NotFoundException::class])
    fun notFoundException(ex: NotFoundException): ModelAndView {
        return ModelAndView("404", "path", ex.path)
    }

    @Secured(Permission.MANAGE_FILES_STR)
    @DeleteMapping("/delete/**")
    fun deleteResource(request: HttpServletRequest): ModelAndView {
        val path = request.servletPath.removePrefix("/delete/")
        val entity = repoService.getEntity(path) ?: throw NotFoundException(path)
        val mountPathElements = if (path.endsWith("/")) path.dropLast(1).split("/") else path.split("/")
        val mountPath = mountPathElements.joinToString("/")
        if (entity is RepositoryDirectory) {
            repoService.delete(entity.relativePath, true)
            return when (mountPathElements.size) {
                1 -> ModelAndView(RedirectView("/file/"))
                else -> ModelAndView(RedirectView("/file/$mountPath/"))
            }
        } else if (entity is RepositoryFile) {
            repoService.delete(entity.relativePath, false)
            return ModelAndView(RedirectView("/file/$mountPath/"))
        }
        return ModelAndView("/file/")
    }
}
