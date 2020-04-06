package pl.edu.uj.ii.ksi.mordor.controllers

import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import pl.edu.uj.ii.ksi.mordor.forms.FileUploadForm

@Controller
class FileManagementController {
    @GetMapping("/management/upload/")
    fun uploadPage(): ModelAndView {
        return ModelAndView("management/upload", "form", FileUploadForm())
    }

    @PostMapping("/management/upload/")
    fun uploadMultipartFile(
        @Valid @ModelAttribute("form") model: FileUploadForm,
        result: BindingResult
    ): ModelAndView {
        if (result.hasErrors()) {
            return ModelAndView("management/upload", HttpStatus.BAD_REQUEST)
        }
        return ModelAndView("home")
    }
}
