package pl.edu.uj.ii.ksi.mordor.events

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import javax.mail.internet.MimeMessage
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.springframework.mail.javamail.JavaMailSender
import pl.edu.uj.ii.ksi.mordor.persistence.entities.EmailVerificationToken
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.EmailVerificationTokenRepository

class EmailVerificationProcessorTest {
    companion object {
        private const val emailFrom = "test@example.com"
        private const val siteName = "https://mordor.example.com"
    }

    private val mockMessage: MimeMessage = mock()
    private val mockSender: JavaMailSender = mock {
        on { createMimeMessage() } doReturn mockMessage
    }
    private val mockTokenRepository: EmailVerificationTokenRepository = mock()

    private val emailVerificationProcessor = EmailVerificationProcessor(mockSender, mockTokenRepository,
        emailFrom, siteName)

    @Test
    fun startEmailVerification() {
        val user = User(0, "jsmith", null, "jsmith@example.com", "John",
            "Smith", false, Role.NOBODY, ArrayList())
        val event = OnEmailVerificationRequestedEvent(user)

        emailVerificationProcessor.startEmailVerification(event)

        val tokenCaptor = ArgumentCaptor.forClass(EmailVerificationToken::class.java)
        verify(mockTokenRepository).save(tokenCaptor.capture())
        assertEquals(user, tokenCaptor.value.user)
        // TODO: test message content and headers.
        verify(mockSender).send(eq(mockMessage))
    }
}
