package pl.edu.uj.ii.ksi.mordor.controllers

import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import pl.edu.uj.ii.ksi.mordor.events.OnEmailVerificationRequestedEvent
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.forms.ResetPasswordForm
import pl.edu.uj.ii.ksi.mordor.forms.UserRegistrationForm
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.EmailVerificationTokenRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import javax.validation.Valid

@Controller
class UserRegistrationController(
    private val userRepository: UserRepository,
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    @GetMapping("/register/")
    fun registerForm(model: Model): ModelAndView {
        return ModelAndView("registration/create_account", "form", UserRegistrationForm())
    }

    @PostMapping("/register/")
    fun registerPost(
        @Valid @ModelAttribute("form") user: UserRegistrationForm,
        result: BindingResult
    ): String {
        if (!user.userName.isEmpty() && userRepository.findByUserName(user.userName) != null) {
            result.rejectValue("userName", "username.exists", "username unavailable")
        }

        if (!user.email.isEmpty() && userRepository.findByEmail(user.email) != null) {
            result.rejectValue("email", "email.exists", "email already in use")
        }

        if (result.hasErrors()) {
            return "registration/create_account"
        }

        val newUser = User(null, user.userName, null, user.email, user.firstName, user.lastName, false)
        userRepository.save(newUser)
        eventPublisher.publishEvent(OnEmailVerificationRequestedEvent(newUser))
        return "registration/verify_email"
    }

    @GetMapping(value = ["/register/activate/"], params = ["token"])
    fun changePasswordWithToken(@RequestParam("token") tokenVal: String): ModelAndView {
        val token = emailVerificationTokenRepository.findByToken(tokenVal)
        return when {
            token == null || !token.isValid() -> {
                token?.let { emailVerificationTokenRepository.delete(it) }
                return ModelAndView("registration/invalid_token", HttpStatus.UNAUTHORIZED)
            }
            else -> ModelAndView("registration/set_password", "form", ResetPasswordForm(token = tokenVal))
        }
    }

    @PostMapping("/register/activate/")
    fun changePasswordWithTokenPost(
        @Valid @ModelAttribute("form") form: ResetPasswordForm,
        result: BindingResult
    ): ModelAndView {
        if (form.password != form.password2) {
            result.rejectValue("password2", "password2.notMatch", "passwords do not match")
        }

        if (result.hasErrors()) {
            return ModelAndView("registration/set_password", HttpStatus.BAD_REQUEST)
        }

        val token = emailVerificationTokenRepository.findByToken(form.token)

        if (token == null || !token.isValid()) {
            token?.let { emailVerificationTokenRepository.delete(it) }
            return ModelAndView("registration/invalid_token", HttpStatus.UNAUTHORIZED)
        }

        val user = token.user!!
        // TODO: move password logic to service
        user.password = "{bcrypt}" + BCryptPasswordEncoder().encode(form.password)
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
        val user = userRepository.findByEmail(email) ?: throw BadRequestException("Unknown email address")
        eventPublisher.publishEvent(OnEmailVerificationRequestedEvent(user))
        return "registration/verify_email"
    }
}
