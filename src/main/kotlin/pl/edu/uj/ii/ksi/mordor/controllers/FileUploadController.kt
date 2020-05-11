package pl.edu.uj.ii.ksi.mordor.controllers

import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.forms.FileUploadForm
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import pl.edu.uj.ii.ksi.mordor.services.upload.session.FileUploadSessionService

@Controller
class FileUploadController(
    private val userRepository: UserRepository,
    private val fileUploadSessionService: FileUploadSessionService
) {
    @Secured(Permission.UPLOAD_STR)
    @GetMapping("/upload/")
    fun fileUploadPage(): ModelAndView {
        return ModelAndView("upload", "form", FileUploadForm())
    }

    @Secured(Permission.UPLOAD_STR)
    @PostMapping("/upload/")
    fun uploadMultipartFile(
        @Valid @ModelAttribute("form") model: FileUploadForm,
        authentication: Authentication,
        result: BindingResult
    ): ModelAndView {
        val username = authentication.name
        val user = userRepository.findByUserName(username)
        if (result.hasErrors()) {
            return ModelAndView("upload", HttpStatus.BAD_REQUEST)
        }
        user?.let {
            val session = fileUploadSessionService.createFileSession(user)
            val repository = fileUploadSessionService.getRepositoryServiceOfSession(session)

            repository.saveFile(model.mountPath, model.file!!.inputStream)
            return ModelAndView(RedirectView("/upload/"))
        }
        throw InternalError("No user for username $username")
    }
}
