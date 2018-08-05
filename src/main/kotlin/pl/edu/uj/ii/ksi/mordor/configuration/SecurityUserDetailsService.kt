package pl.edu.uj.ii.ksi.mordor.configuration

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.models.repositories.UserRepository

@Service
class SecurityUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
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
                return user.username
            }

            override fun getPassword(): String {
                return user.password
            }

        }
    }
}
