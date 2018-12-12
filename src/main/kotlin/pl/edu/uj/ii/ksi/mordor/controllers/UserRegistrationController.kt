package pl.edu.uj.ii.ksi.mordor.controllers

import javax.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
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
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.EmailVerificationTokenRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import pl.edu.uj.ii.ksi.mordor.services.UserManagerService

@Controller
class UserRegistrationController(
    private val userRepository: UserRepository,
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${mordor.allow_user_registration:true}") private val registrationEnabled: Boolean,
    private val userManagerService: UserManagerService
) {
    @GetMapping("/register/")
    fun registerForm(model: Model): ModelAndView {
        return if (registrationEnabled) {
            ModelAndView("registration/create_account", "form", UserRegistrationForm())
        } else {
            ModelAndView("registration/registration_disabled")
        }
    }

    @PostMapping("/register/")
    fun registerPost(
        @Valid @ModelAttribute("form") user: UserRegistrationForm,
        result: BindingResult
    ): ModelAndView {
        if (!registrationEnabled) {
            return ModelAndView("registration/registration_disabled", HttpStatus.FORBIDDEN)
        }

        if (!user.userName.isBlank() && userRepository.findByUserName(user.userName) != null) {
            result.rejectValue("userName", "username.exists", "username unavailable")
        }

        if (!user.email.isBlank() && userRepository.findByEmail(user.email) != null) {
            result.rejectValue("email", "email.exists", "email already in use")
        }

        if (result.hasErrors()) {
            return ModelAndView("registration/create_account", HttpStatus.BAD_REQUEST)
        }

        val newUser = User(null, user.userName, null, user.email, user.firstName, user.lastName, true)
        userRepository.save(newUser)
        eventPublisher.publishEvent(OnEmailVerificationRequestedEvent(newUser))
        return ModelAndView("registration/verify_email")
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

        return if (userManagerService.resetPassword(form.token, form.password)) {
            ModelAndView("registration/account_activated")
        } else {
            ModelAndView("registration/invalid_token", HttpStatus.UNAUTHORIZED)
        }
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
