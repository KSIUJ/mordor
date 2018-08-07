package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.models.repositories.UserRepository

@Service
class LocalUserService(val userRepository: UserRepository)
    : UserDetailsService {

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
