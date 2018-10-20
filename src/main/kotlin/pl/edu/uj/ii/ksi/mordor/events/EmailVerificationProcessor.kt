package pl.edu.uj.ii.ksi.mordor.events

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import pl.edu.uj.ii.ksi.mordor.persistence.entities.EmailVerificationToken
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.EmailVerificationTokenRepository
import java.util.*

@Component
class EmailVerificationProcessor(private val mailSender: JavaMailSender,
                                 private val tokenRepository: EmailVerificationTokenRepository,
                                 @Value("\${mordor.mail.from}") private val emailFrom: String,
                                 @Value("\${mordor.site.address}") private val siteAddress: String) {
    private val EXPIRATION_TIME_S = 60 * 60 * 24

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
        helper.setFrom(emailFrom)
        helper.setTo(user.email!!)
        helper.setSubject("Verify your email")
        val confirmationUri = siteAddress + "/register/activate/?token=" + token.token
        val content = "Hello ${user.firstName} ${user.lastName}!<br>" +
                "Please go to <a href=\"$confirmationUri\">$confirmationUri</a> to verify your email"
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
