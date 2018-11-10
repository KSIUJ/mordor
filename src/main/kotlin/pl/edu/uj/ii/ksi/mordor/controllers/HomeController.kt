package pl.edu.uj.ii.ksi.mordor.controllers

import java.security.Principal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping(value = ["/"])
    fun homePage(principal: Principal?): String {
        return if (principal != null) {
            "redirect:/file/"
        } else {
            "home"
        }
    }
}
