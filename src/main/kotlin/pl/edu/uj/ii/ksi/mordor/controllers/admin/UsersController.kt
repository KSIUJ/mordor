package pl.edu.uj.ii.ksi.mordor.controllers.admin

import org.springframework.data.domain.PageRequest
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View
import org.springframework.web.servlet.view.RedirectView
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository

@Controller
class UsersController(private val userRepository: UserRepository) {
    companion object {
        private const val usersPerPage = 100
    }

    @Secured(Permission.MANAGE_USERS_STR)
    @GetMapping("/admin/users/")
    fun userList(): View {
        return RedirectView("/admin/users/0/")
    }

    @Secured(Permission.MANAGE_USERS_STR)
    @GetMapping("/admin/users/{num}/")
    fun userList(@PathVariable("num") pageNumber: Int): ModelAndView {
        val users = userRepository.findAll(PageRequest.of(pageNumber, usersPerPage))
        return ModelAndView("admin/user_list", "users", users)
    }
}
