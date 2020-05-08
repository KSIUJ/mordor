package pl.edu.uj.ii.ksi.mordor.controllers.session

import org.springframework.stereotype.Service
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriUtils
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryFile
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionRepository

@Service
class ReviewPreviewFactory(
    private val fileUploadSessionRepository: FileUploadSessionRepository
) {

    private fun createTitle(file: RepositoryFile): String {
        return "Preview of file: " + file.name
    }

    private fun previewPage(file: RepositoryFile, path: String, sessionId: String, userId: Long): ModelAndView {
        return ModelAndView("review/preview", mapOf(
                "title" to createTitle(file),
                "raw" to "/raw/$path",
                "path" to createBreadcrumb(file, sessionId, userId),
                "download" to "/download/${file.relativePath}",
                "type" to SessionReviewController.FileType.PAGE.previewType,
                "sessionId" to sessionId,
                "userId" to userId
        ))
    }

    fun previewFor(file: RepositoryFile, userId: Long, sessionId: String): ModelAndView {
        if (file is RepositoryFile) {
            when {
                file.isPage ->
                    return previewPage(file, entityRelativePath, sessionId, userId)
                file.mimeType.startsWith("text/") || entity.isCode ->
                    return previewText(file, entityRelativePath, sessionId, userId)
                file.isDisplayableImage ->
                    return previewImage(file, entityRelativePath, sessionId, userId)
            }
        }
        return ModelAndView(RedirectView(urlEncodePath("/review/download/${file.relativePath}")))
    }

    private fun urlEncodePath(path: String): String {
        return UriUtils.encodePath(path, "UTF-8")
    }
}