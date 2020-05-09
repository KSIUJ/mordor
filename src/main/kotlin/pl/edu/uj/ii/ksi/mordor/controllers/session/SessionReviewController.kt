package pl.edu.uj.ii.ksi.mordor.controllers.session

import javax.servlet.http.HttpServletRequest
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriUtils
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException as BadRequestException
import pl.edu.uj.ii.ksi.mordor.exceptions.NotFoundException
import pl.edu.uj.ii.ksi.mordor.model.SessionEntry as SessionEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionRepository
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionService

@Controller
class SessionReviewController(
    private val userRepository: UserRepository,
    private val fileUploadSessionService: FileUploadSessionService,
    private val sessionRepository: FileUploadSessionRepository,
    private val previewFactory: ReviewViewsFactory
) {
    @Secured(Permission.MANAGE_FILES_STR)
    @GetMapping("/review/")
    fun sessionReviewList(): ModelAndView {
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
    @GetMapping("/review/files/{userId}/{sessionId}/**")
    fun sessionReviewPage(
        @PathVariable("userId") userId: Long,
        @PathVariable("sessionId") sessionId: String,
        request: HttpServletRequest
    ): ModelAndView {
        val session = sessionRepository.findById(Pair(userId, sessionId))
        if (session.isPresent) {
            val repository = fileUploadSessionService.getRepositoryServiceOfSession(session.get())
            val path = request.servletPath.removePrefix("/review/files/$userId/$sessionId/")
            val entity = repository.getEntity(path) ?: throw NotFoundException("Session with $sessionId")
            if (entity is RepositoryDirectory) {
                if (!request.servletPath.endsWith("/")) {
                    return ModelAndView(RedirectView(urlEncodePath(request.servletPath + "/")))
                }
                return previewFactory.listFor(entity, userId, sessionId)
            }
            return previewFactory.previewFor(entity, userId, sessionId)
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
