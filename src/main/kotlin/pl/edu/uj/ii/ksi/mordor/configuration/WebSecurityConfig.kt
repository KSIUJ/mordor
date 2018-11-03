package pl.edu.uj.ii.ksi.mordor.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.RememberMePersistentTokenRepository
import pl.edu.uj.ii.ksi.mordor.services.LdapRolePopulator
import pl.edu.uj.ii.ksi.mordor.services.LocalUserService

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val tokenRepository: RememberMePersistentTokenRepository,
    private val userService: LocalUserService,
    @Value("\${mordor.secret}") private val secret: String,
    @Value("\${mordor.ldap.url:}") private val ldapUrl: String,
    @Value("\${mordor.ldap.userdn:}") private val userDnPatterns: String,
    private val ldapRolePopulator: LdapRolePopulator
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests().antMatchers("/", "/register/**").permitAll()
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login/").permitAll()
            .and().logout().logoutUrl("/logout/").permitAll()
            .and().rememberMe().key(secret).tokenRepository(tokenRepository).userDetailsService(userService)
    }

    override fun configure(web: WebSecurity) {
        web.ignoring()
            .antMatchers("/static/**")
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService)

        if (ldapUrl.isNotEmpty()) {
            auth.ldapAuthentication()
                .userDnPatterns(userDnPatterns)
                .contextSource().url(ldapUrl)
                .and().ldapAuthoritiesPopulator(ldapRolePopulator)
        }
    }
}
