package pl.edu.uj.ii.ksi.mordor.controllers

import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.forms.FileUploadForm
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionService

@Controller
class FileManagementController(
    private val userRepository: UserRepository,
    private val fileUploadSessionService: FileUploadSessionService,
    private val repositoryService: RepositoryService
) {
    data class SessionEntry(
        val userId: Long,
        val userName: String,
        val sessionId: String
    )

    @GetMapping("/management/upload/")
    fun fileUploadPage(): ModelAndView {
        return ModelAndView("management/upload", "form", FileUploadForm())
    }

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
            repository.saveFile(model.mountPath, model.file!!.inputStream)
            return ModelAndView(RedirectView("/management/upload/"))
        }
        throw BadRequestException("No user for username $username")
    }

    @GetMapping("/review/")
    fun sessionReviewList(): ModelAndView {
        val sessions = arrayOf(SessionEntry(1, "jaki", "4567"))

        // TODO: - sort by timestamp
        val sortedSessions = sessions.sortedBy { it.userName }
        return ModelAndView("review/list", mapOf(
                "sessions" to sortedSessions
        ))
    }

    @GetMapping("/review/{userId}/{sessionId}/")
    fun sessionReviewPage(
        @PathVariable("userId") userId: Long,
        @PathVariable("sessionId") sessionId: String
    ): ModelAndView {
        // TODO: - fetch list of item when merged with ms-1
        return ModelAndView("review/session_review", mapOf(
                "sessionId" to sessionId,
                "userId" to userId,
                "files" to listOf<FilesystemController.FileEntry>()
        ))
//        val user = userRepository.findById(userId)
//        if (user.isPresent) {
//            // TODO: - Create session entry
//        } else {
//            throw BadRequestException("No user for id: $userId")
//        }
    }

    @PostMapping("/review/approve/{userId}/{sessionId}/")
    fun approveSession(
        @PathVariable("userId") userId: Long,
        @PathVariable("sessionId") sessionId: String
    ): ModelAndView {
        val user = userRepository.findById(userId)
        if (user.isPresent) {
            // TODO: - Create session entry when merged with ms-1
            return ModelAndView(RedirectView("/review/"))
        } else {
            throw BadRequestException("No user for id: $userId")
        }
    }

    @PostMapping("/review/reject/{userId}/{sessionId}/")
    fun rejectSession(
        @PathVariable("userId") userId: Long,
        @PathVariable("sessionId") sessionId: String
    ): ModelAndView {
        val user = userRepository.findById(userId)
        if (user.isPresent) {
            // TODO: - Create session entry when merged with ms-1
            return ModelAndView(RedirectView("/review/"))
        } else {
            throw BadRequestException("No user for id: $userId")
        }
    }
}
