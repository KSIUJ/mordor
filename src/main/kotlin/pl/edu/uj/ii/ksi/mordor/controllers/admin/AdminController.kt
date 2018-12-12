package pl.edu.uj.ii.ksi.mordor.controllers.admin

import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.View
import org.springframework.web.servlet.view.RedirectView
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission

@Controller
class AdminController {
    @Secured(Permission.ACCESS_ADMIN_PANEL_STR)
    @GetMapping("/admin/")
    fun adminPage(): View {
        return RedirectView("/admin/users/")
    }
}
