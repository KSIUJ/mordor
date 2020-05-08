package pl.edu.uj.ii.ksi.mordor.controllers.session

import javax.servlet.http.HttpServletRequest
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriUtils
import pl.edu.uj.ii.ksi.mordor.controllers.FilesystemController
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException as BadRequestException
import pl.edu.uj.ii.ksi.mordor.exceptions.NotFoundException
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import pl.edu.uj.ii.ksi.mordor.services.IconNameProvider
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryFile
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionRepository
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionService

@Controller
class SessionReviewController(
    private val userRepository: UserRepository,
    private val fileUploadSessionService: FileUploadSessionService,
    private val sessionRepository: FileUploadSessionRepository,
    private val iconNameProvider: IconNameProvider,
    private val fileUploadSessionRepository: FileUploadSessionRepository,
    @Value("\${mordor.list_hidden_files:false}") private val listHiddenFiles: Boolean,
    @Value("\${mordor.preview.max_text_bytes:1048576}") private val maxTextBytes: Int,
    @Value("\${mordor.preview.max_image_bytes:10485760}") private val maxImageBytes: Int
) {
    data class SessionEntry(
        val userId: Long,
        val userName: String,
        val sessionId: String
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

    private fun createTitle(path: String): String {
        return path.trim('/').replace("/", " / ") + (if (path.isNotEmpty()) " - " else "") + "Mordor"
    }

    private fun createBreadcrumb(entity: RepositoryEntity, sessionId: String, userId: Long): List<RelativeDir> {
        val pathBreadcrumb = mutableListOf(RelativeDir("Home", "/review/$userId/$sessionId/"))
        var prev = "/review/$userId/$sessionId/"
        entity.relativePath.split('/').forEach { dir ->
            pathBreadcrumb.add(RelativeDir(dir, prev + dir))
            prev = "$prev$dir/"
        }
        return pathBreadcrumb
    }

    private fun previewText(entity: RepositoryFile, path: String, sessionId: String, userId: Long): ModelAndView {
        if (entity.file.length() > maxTextBytes) {
            return ModelAndView("review/preview", mapOf(
                    "title" to createTitle(path),
                    "path" to createBreadcrumb(entity, sessionId, userId),
                    "download" to "/download/${entity.relativePath}",
                    "type" to FileType.TOO_LARGE.previewType,
                    "sessionId" to sessionId,
                    "userId" to userId
            ))
        }
        val text = FileUtils.readFileToString(entity.file, "utf-8")
        // TODO: detect encoding
        return ModelAndView("review/preview", mapOf(
                "title" to createTitle(path),
                "text" to text,
                "path" to createBreadcrumb(entity, sessionId, userId),
                "download" to "/download/${entity.relativePath}",
                "type" to FileType.CODE.previewType,
                "sessionId" to sessionId,
                "userId" to userId
        ))
    }

    private fun previewPage(entity: RepositoryFile, path: String, sessionId: String, userId: Long): ModelAndView {
        return ModelAndView("review/preview", mapOf(
                "title" to createTitle(path),
                "raw" to "/raw/$path",
                "path" to createBreadcrumb(entity, sessionId, userId),
                "download" to "/download/${entity.relativePath}",
                "type" to FileType.PAGE.previewType,
                "sessionId" to sessionId,
                "userId" to userId
        ))
    }

    private fun previewImage(entity: RepositoryFile, path: String, sessionId: String, userId: Long): ModelAndView {
        if (entity.file.length() > maxImageBytes) {
            return ModelAndView("review/preview", mapOf(
                    "title" to createTitle(path),
                    "path" to createBreadcrumb(entity, sessionId, userId),
                    "download" to "/download/${path}",
                    "type" to FileType.TOO_LARGE.previewType,
                    "sessionId" to sessionId,
                    "userId" to userId
            ))
        }
        return ModelAndView("review/preview", mapOf(
                "title" to createTitle(path),
                "path" to createBreadcrumb(entity, sessionId, userId),
                "download" to "/download/${path}",
                "type" to FileType.IMAGE.previewType,
                "sessionId" to sessionId,
                "userId" to userId
        ))
    }

    @Secured(Permission.MANAGE_FILES_STR)
    @GetMapping("/review/")
    fun sessionRevewiList(): ModelAndView {
        val sessions = sessionRepository.findAll()
        val sessionEntries = sessions.mapNotNull { session ->
            session.user.id?.let { it -> SessionEntry(it, session.user.userName, session.id) }
        }
        val sortedEntries = sessionEntries.sortedBy { it.userName }
        return ModelAndView("review/list", mapOf(
                "sessions" to sortedEntries
        ))
    }

    @Secured(Permission.MANAGE_FILES_STR)
    @GetMapping("/review/{userId}/{sessionId}/**")
    fun sessionReviewPage(
        @PathVariable("userId") userId: Long,
        @PathVariable("sessionId") sessionId: String,
        request: HttpServletRequest
    ): ModelAndView {
        val session = sessionRepository.findById(Pair(userId, sessionId))
        if (session.isPresent) {
            val repository = fileUploadSessionService.getRepositoryServiceOfSession(session.get())
            val path = request.servletPath.removePrefix("/review/$userId/$sessionId/")
            val entity = repository.getEntity(path) ?: throw NotFoundException("Session with $sessionId")
            val entityRelativePath = fileUploadSessionRepository.getPathOfId(Pair(userId, sessionId)) + "/" + path
            val canListHidden = listHiddenFiles || SecurityContextHolder.getContext().authentication.authorities
                    .contains(Permission.ROLE_LIST_HIDDEN_FILES)
            if (entity is RepositoryDirectory) {
                if (!request.servletPath.endsWith("/")) {
                    return ModelAndView(RedirectView(urlEncodePath(request.servletPath + "/")))
                }
                val children = entity.getChildren(canListHidden)
                        .sortedBy { it.name }
                        .map { entry ->
                            FilesystemController.FileEntry(entry.relativePath,
                                    entry.name, iconNameProvider.getIconName(entry))
                        }
                return ModelAndView("review/session_review", mapOf(
                        "sessionId" to sessionId,
                        "userId" to userId,
                        "files" to children
                ))
            } else if (entity is RepositoryFile) {
                when {
                entity.isPage ->
                    return previewPage(entity, entityRelativePath, sessionId, userId)
                entity.mimeType.startsWith("text/") || entity.isCode ->
                    return previewText(entity, entityRelativePath, sessionId, userId)
                entity.isDisplayableImage ->
                    return previewImage(entity, entityRelativePath, sessionId, userId)
                }
            }
                return ModelAndView(RedirectView(urlEncodePath("/download/$entityRelativePath")))
        }
        throw BadRequestException("Session entity is invalid")
    }

    @Secured(Permission.MANAGE_FILES_STR)
    @PostMapping("/review/approve/{userId}/{sessionId}/")
    fun approveSession(
        @PathVariable("userId") userId: Long,
        @PathVariable("sessionId") sessionId: String
    ): ModelAndView {
        val user = userRepository.findById(userId)
        if (user.isPresent) {
            val session = sessionRepository.findById(Pair(userId, sessionId))
            if (session.isPresent) {
                fileUploadSessionService.approve(session.get())
                return ModelAndView(RedirectView("/review/"))
            } else {
                throw BadRequestException("No session found for user: $userId, session: $sessionId")
            }
        } else {
            throw BadRequestException("No user for id: $userId")
        }
    }

    @Secured(Permission.MANAGE_FILES_STR)
    @PostMapping("/review/reject/{userId}/{sessionId}/")
    fun rejectSession(
        @PathVariable("userId") userId: Long,
        @PathVariable("sessionId") sessionId: String
    ): ModelAndView {
        val user = userRepository.findById(userId)
        if (user.isPresent) {
            val session = sessionRepository.findById(Pair(userId, sessionId))
            if (session.isPresent) {
                fileUploadSessionService.reject(session.get())
                return ModelAndView(RedirectView("/review/"))
            } else {
                throw BadRequestException("No session found for user: $userId, session: $sessionId")
            }
        } else {
            throw BadRequestException("No user for id: $userId")
        }
    }

    private fun urlEncodePath(path: String): String {
        return UriUtils.encodePath(path, "UTF-8")
    }
}
