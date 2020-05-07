package pl.edu.uj.ii.ksi.mordor.controllers

import javax.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException as BadRequestException
import pl.edu.uj.ii.ksi.mordor.exceptions.NotFoundException
import pl.edu.uj.ii.ksi.mordor.forms.FileUploadForm
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import pl.edu.uj.ii.ksi.mordor.services.IconNameProvider
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionRepository
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionService

@Controller
class FileManagementController(
    private val userRepository: UserRepository,
    private val fileUploadSessionService: FileUploadSessionService,
    private val sessionRepository: FileUploadSessionRepository,
    private val iconNameProvider: IconNameProvider,
    @Value("\${mordor.list_hidden_files:false}") private val listHiddenFiles: Boolean
) {
    data class SessionEntry(
        val userId: Long,
        val userName: String,
        val sessionId: String
    )

    @Secured(Permission.UPLOAD_STR)
    @GetMapping("/management/upload/")
    fun fileUploadPage(): ModelAndView {
        return ModelAndView("management/upload", "form", FileUploadForm())
    }

    @Secured(Permission.UPLOAD_STR)
    @PostMapping("/management/upload/")
    fun uploadMultipartFile(
        @Valid @ModelAttribute("form") model: FileUploadForm,
        authentication: Authentication,
        result: BindingResult
    ): ModelAndView {
        val username = authentication.name
        val user = userRepository.findByUserName(username)
        if (result.hasErrors()) {
            return ModelAndView("management/upload", HttpStatus.BAD_REQUEST)
        }
        user?.let {
            val session = fileUploadSessionService.createFileSession(user)
            val repository = fileUploadSessionService.getRepositoryServiceOfSession(session)

            // TODO make savefile throwing func
            repository.saveFile(model.mountPath, model.file!!.inputStream)
            return ModelAndView(RedirectView("/management/upload/"))
        }
        throw BadRequestException("No user for username $username")
    }

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
    @GetMapping("/review/{userId}/{sessionId}/")
    fun sessionReviewPage(
        @PathVariable("userId") userId: Long,
        @PathVariable("sessionId") sessionId: String
    ): ModelAndView {

        val session = sessionRepository.findById(Pair(userId, sessionId))
        if (session.isPresent) {
            val repository = fileUploadSessionService.getRepositoryServiceOfSession(session.get())
            val sessionPath = sessionRepository.getPathOfSession(session.get())
            val entity = repository.getEntity(sessionPath) ?: throw NotFoundException(sessionPath)
            val canListHidden = listHiddenFiles || SecurityContextHolder.getContext().authentication.authorities
                    .contains(Permission.ROLE_LIST_HIDDEN_FILES)
            return if (entity is RepositoryDirectory) {
                val children = entity.getChildren(canListHidden)
                        .sortedBy { it.name }
                        .map { entry ->
                            FilesystemController.FileEntry(entry.relativePath,
                                    entry.name, iconNameProvider.getIconName(entry))
                        }
                ModelAndView("review/session_review", mapOf(
                        "sessionId" to sessionId,
                        "userId" to userId,
                        "files" to children
                ))
            } else {
                throw BadRequestException("Session entity is invalid")
            }
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
}
