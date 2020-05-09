package pl.edu.uj.ii.ksi.mordor.controllers.session

import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriUtils
import pl.edu.uj.ii.ksi.mordor.controllers.FilesystemController
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission
import pl.edu.uj.ii.ksi.mordor.services.IconNameProvider
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryFile
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionRepository
import pl.edu.uj.ii.ksi.mordor.model.RelativeDirectory as RelativeDirectory
import pl.edu.uj.ii.ksi.mordor.model.FileType as FileType

@Service
class ReviewViewsFactory(
        private val fileUploadSessionRepository: FileUploadSessionRepository,
        private val iconNameProvider: IconNameProvider,
        @Value("\${mordor.preview.max_image_bytes:10485760}") private val maxImageBytes: Int,
        @Value("\${mordor.preview.max_text_bytes:1048576}") private val maxTextBytes: Int,
        @Value("\${mordor.list_hidden_files:false}") private val listHiddenFiles: Boolean
) {

    private fun createTitle(file: RepositoryFile): String {
        return "Preview of file: " + file.name
    }

    private fun createBreadcrumb(entity: RepositoryEntity, sessionId: String, userId: Long): List<RelativeDirectory> {
        val pathBreadcrumb = mutableListOf(RelativeDirectory("Session files", "/review/$userId/$sessionId/"))
        var prev = "/review/$userId/$sessionId/"
        entity.relativePath.split('/').forEach { dir ->
            pathBreadcrumb.add(RelativeDirectory(dir, prev + dir))
            prev = "$prev$dir/"
        }
        return pathBreadcrumb
    }

    private fun previewImage(file: RepositoryFile, path: String, sessionId: String, userId: Long): ModelAndView {
        if (file.file.length() > maxImageBytes) {
            return ModelAndView("review/preview", mapOf(
                    "title" to createTitle(file),
                    "path" to createBreadcrumb(file, sessionId, userId),
                    "download" to "/download/${path}",
                    "type" to FileType.TOO_LARGE.previewType,
                    "sessionId" to sessionId,
                    "userId" to userId
            ))
        }
        return ModelAndView("review/preview", mapOf(
                "title" to createTitle(file),
                "path" to createBreadcrumb(file, sessionId, userId),
                "download" to "/download/${path}",
                "type" to FileType.IMAGE.previewType,
                "sessionId" to sessionId,
                "userId" to userId
        ))
    }

    private fun previewText(file: RepositoryFile, path: String, sessionId: String, userId: Long): ModelAndView {
        if (file.file.length() > maxTextBytes) {
            return ModelAndView("review/preview", mapOf(
                    "title" to createTitle(file),
                    "path" to createBreadcrumb(file, sessionId, userId),
                    "download" to "/download/${path}",
                    "type" to FileType.TOO_LARGE.previewType,
                    "sessionId" to sessionId,
                    "userId" to userId
            ))
        }
        val text = FileUtils.readFileToString(file.file, "utf-8")
        // TODO: detect encoding
        return ModelAndView("review/preview", mapOf(
                "title" to createTitle(file),
                "text" to text,
                "path" to createBreadcrumb(file, sessionId, userId),
                "download" to "/download/${path}",
                "type" to FileType.CODE.previewType,
                "sessionId" to sessionId,
                "userId" to userId
        ))
    }

    private fun previewPage(file: RepositoryFile, path: String, sessionId: String, userId: Long): ModelAndView {
        return ModelAndView("review/preview", mapOf(
                "title" to createTitle(file),
                "raw" to "/raw/$path",
                "path" to createBreadcrumb(file, sessionId, userId),
                "download" to "/download/${path}",
                "type" to FileType.PAGE.previewType,
                "sessionId" to sessionId,
                "userId" to userId
        ))
    }

    fun previewFor(entity: RepositoryEntity, userId: Long, sessionId: String): ModelAndView {
        val sessionRepositoryPath = fileUploadSessionRepository.getPathOfId(Pair(userId, sessionId))
        val entityPath = sessionRepositoryPath + "/" + entity.relativePath
        if (entity is RepositoryFile) {
            when {
                entity.isPage ->
                    return previewPage(entity, entityPath, sessionId, userId)
                entity.mimeType.startsWith("text/") || entity.isCode ->
                    return previewText(entity, entityPath, sessionId, userId)
                entity.isDisplayableImage ->
                    return previewImage(entity, entityPath, sessionId, userId)
            }
        }
        return ModelAndView(RedirectView(urlEncodePath("/review/download/${entity.relativePath}")))
    }

    fun listFor(entity: RepositoryDirectory, userId: Long, sessionId: String): ModelAndView {
        val canListHidden = listHiddenFiles || SecurityContextHolder.getContext().authentication.authorities
                .contains(Permission.ROLE_LIST_HIDDEN_FILES)

        val children = entity.getChildren(canListHidden)
                .sortedBy { it.name }
                .map { entry ->
                    FilesystemController.FileEntry(entry.relativePath,
                            entry.name, iconNameProvider.getIconName(entry))
                }
        return ModelAndView("review/session_review", mapOf(
                "sessionId" to sessionId,
                "userId" to userId,
                "files" to children,
                "path" to  createBreadcrumb(entity, sessionId, userId)
        ))
    }

    private fun urlEncodePath(path: String): String {
        return UriUtils.encodePath(path, "UTF-8")
    }
}