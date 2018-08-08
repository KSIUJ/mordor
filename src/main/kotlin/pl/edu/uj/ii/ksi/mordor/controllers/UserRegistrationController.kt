package pl.edu.uj.ii.ksi.mordor.controllers

import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import pl.edu.uj.ii.ksi.mordor.events.OnEmailVerificationRequestedEvent
import pl.edu.uj.ii.ksi.mordor.models.entities.Role
import pl.edu.uj.ii.ksi.mordor.models.entities.User
import pl.edu.uj.ii.ksi.mordor.models.repositories.EmailVerificationTokenRepository
import pl.edu.uj.ii.ksi.mordor.models.repositories.UserRepository

@Controller
class UserRegistrationController(private val userRepository: UserRepository,
                                 private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
                                 private val eventPublisher: ApplicationEventPublisher) {

    @GetMapping("/register/")
    fun registerForm(model: Model): ModelAndView {
        return ModelAndView("registration/create_account", "user", User())
    }

    @PostMapping("/register/")
    fun registerPost(@ModelAttribute user: User): String {
        if (userRepository.findByUserName(user.userName) != null) {
            //TODO: gracefully handle this.
            throw RuntimeException("Username already exists")
        }
        userRepository.save(user)
        eventPublisher.publishEvent(OnEmailVerificationRequestedEvent(user))
        return "registration/verify_email"
    }

    @GetMapping(value = ["/register/activate/"], params = ["token"])
    fun changePasswordWithToken(@RequestParam("token") tokenVal: String): ModelAndView {
        val token = emailVerificationTokenRepository.findByToken(tokenVal)
        if (token == null) {
            //TODO: gracefully handle this.
            throw RuntimeException("Invalid token")
        }
        if (!token.isValid()) {
            throw RuntimeException("Token expired")
        }
        return ModelAndView("registration/set_password", "token", tokenVal)
    }

    @PostMapping("/register/activate/")
    fun changePasswordWithTokenPost(@ModelAttribute("token") tokenVal: String,
                                    @ModelAttribute("password") password: String): ModelAndView {
        val token = emailVerificationTokenRepository.findByToken(tokenVal)
        if (token == null) {
            //TODO: gracefully handle this.
            throw RuntimeException("Invalid token")
        }
        if (!token.isValid()) {
            emailVerificationTokenRepository.delete(token)
            throw RuntimeException("Token expired")
            //TODO: resend token if expired
        }

        val user = token.user!!
        // TODO: move password logic to service
        user.password = BCryptPasswordEncoder().encode(password)
        user.enabled = true
        if (user.role == Role.ROLE_NOBODY) {
            user.role = Role.ROLE_USER
        }
        userRepository.save(user)
        emailVerificationTokenRepository.delete(token)

        return ModelAndView("registration/account_activated")
    }

    @GetMapping("/register/reset/")
    fun requestPasswordReset(): String {
        return "registration/reset_password"
    }

    @PostMapping("/register/reset/")
    fun requestPasswordResetPost(@ModelAttribute("email") email: String): String {
        val user = userRepository.findByEmail(email)
        if (user == null) {
            //TODO: gracefully handle this.
            throw RuntimeException("Unknown email address")
        }
        eventPublisher.publishEvent(OnEmailVerificationRequestedEvent(user))
        return "registration/verify_email"
    }
}
