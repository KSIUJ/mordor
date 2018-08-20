package pl.edu.uj.ii.ksi.mordor.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import pl.edu.uj.ii.ksi.mordor.models.repositories.RememberMePersistentTokenRepository
import pl.edu.uj.ii.ksi.mordor.services.LocalUserService


@Configuration
@EnableWebSecurity
class WebSecurityConfig(val tokenRepository: RememberMePersistentTokenRepository,
                        val userService: LocalUserService,
                        @Value("\${mordor.secret}") val secret: String)
    : WebSecurityConfigurerAdapter() {

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
}
