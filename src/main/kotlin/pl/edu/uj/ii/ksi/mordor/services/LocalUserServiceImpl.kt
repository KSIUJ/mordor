package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.context.ApplicationListener
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.events.OnEmailVerificationRequestedEvent
import pl.edu.uj.ii.ksi.mordor.models.entities.EmailVerificationToken
import pl.edu.uj.ii.ksi.mordor.models.repositories.EmailVerificationTokenRepository
import pl.edu.uj.ii.ksi.mordor.models.repositories.UserRepository
import java.util.*

@Service
class LocalUserServiceImpl(val userRepository: UserRepository, val javaMailSender: JavaMailSender, val tokenRepository: EmailVerificationTokenRepository)
    : UserDetailsService, ApplicationListener<OnEmailVerificationRequestedEvent> {
    private val EXPIRATION_TIME_S = 60 * 60 * 24

    override fun onApplicationEvent(event: OnEmailVerificationRequestedEvent) {
        val user = event.user
        val expiryDate = calculateExpiryDate(EXPIRATION_TIME_S)
        val token = EmailVerificationToken(null, UUID.randomUUID().toString(), expiryDate, user)
        tokenRepository.save(token)
        System.out.println(token.token)

        // TODO: send email
    }

    private fun calculateExpiryDate(expiryTimeInSeconds: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.SECOND, expiryTimeInSeconds)
        return calendar.time
    }

    override fun loadUserByUsername(userName: String): UserDetails {
        val user = userRepository.findByUserName(userName) ?: throw UsernameNotFoundException(userName)
        return object : UserDetails {
            override fun getAuthorities(): Collection<GrantedAuthority> {
                return user.role.permissions
            }

            override fun isEnabled(): Boolean {
                return user.enabled
            }

            override fun isCredentialsNonExpired(): Boolean {
                return true
            }

            override fun isAccountNonExpired(): Boolean {
                return true
            }

            override fun isAccountNonLocked(): Boolean {
                return true
            }

            override fun getUsername(): String {
                return user.userName
            }

            override fun getPassword(): String? {
                return user.password
            }

        }
    }
}
