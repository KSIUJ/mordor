package pl.edu.uj.ii.ksi.mordor.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping(value = ["/"])
    fun homePage(): String {
        return "home"
    }
}
