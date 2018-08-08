package pl.edu.uj.ii.ksi.mordor.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {
    @GetMapping(value = ["/login/"])
    fun homePage(): String {
        return "login"
    }
}
