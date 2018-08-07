package pl.edu.uj.ii.ksi.mordor.events

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import pl.edu.uj.ii.ksi.mordor.models.entities.EmailVerificationToken
import pl.edu.uj.ii.ksi.mordor.models.repositories.EmailVerificationTokenRepository
import java.util.*

@Component
class EmailVerificationProcessor(val mailSender: JavaMailSender, val tokenRepository: EmailVerificationTokenRepository) {
    private val EXPIRATION_TIME_S = 60 * 60 * 24

    @Value("\${mordor.mail.from}")
    private lateinit var VERIFICATION_EMAIL_FROM: String

    @Value("\${mordor.site.address}")
    private lateinit var SITE_ADDRESS: String

    private val log = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    @Async
    fun startEmailVerification(event: OnEmailVerificationRequestedEvent) {
        val user = event.user
        val expiryDate = calculateExpiryDate(EXPIRATION_TIME_S)
        val token = EmailVerificationToken(null, UUID.randomUUID().toString(), expiryDate, user)
        tokenRepository.save(token)
        log.info("New token created for ${user.userName}: ${token.token}")

        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, false, "utf-8")
        helper.setFrom(VERIFICATION_EMAIL_FROM)
        helper.setTo(user.email!!)
        helper.setSubject("Verify your email")
        val confirmationUri = SITE_ADDRESS + "/register/activate/?token=" + token.token
        val content = "Go to <a href=\"$confirmationUri\">$confirmationUri<\\a>"
        message.setContent(content, "text/html")
        mailSender.send(message)
    }

    private fun calculateExpiryDate(expiryTimeInSeconds: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.SECOND, expiryTimeInSeconds)
        return calendar.time
    }
}
