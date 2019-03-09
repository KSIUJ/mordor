package pl.edu.uj.ii.ksi.mordor.controllers

import java.security.Principal
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.View
import org.springframework.web.servlet.view.RedirectView

@Controller
class LoginController(@Value("\${mordor.site.address:}") private val siteUrl: String) {
    @GetMapping(value = ["/login/"])
    fun loginPage(principal: Principal?): String {
        return if (principal != null) {
            "redirect:/file/"
        } else {
            "login"
        }
    }

    @Secured
    @GetMapping(value = ["/cas"])
    fun casLogin(@RequestParam("redirect") redirect: String): View {
        return RedirectView(siteUrl + redirect)
    }
}
