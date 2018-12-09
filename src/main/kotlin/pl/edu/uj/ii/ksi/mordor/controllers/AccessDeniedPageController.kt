package pl.edu.uj.ii.ksi.mordor.controllers

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class AccessDeniedPageController {
    @GetMapping("/403/")
    @ExceptionHandler(value = [AccessDeniedException::class])
    fun accessDenied(): ModelAndView {
        return ModelAndView("403")
    }
}
