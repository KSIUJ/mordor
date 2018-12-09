package pl.edu.uj.ii.ksi.mordor.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient
import org.springframework.security.ldap.DefaultSpringSecurityContextSource
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch
import org.springframework.security.ldap.userdetails.LdapUserDetailsService
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.RememberMePersistentTokenRepository
import pl.edu.uj.ii.ksi.mordor.services.LdapRolePopulator
import pl.edu.uj.ii.ksi.mordor.services.LocalUserService

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class WebSecurityConfig(
    private val tokenRepository: RememberMePersistentTokenRepository,
    private val userService: LocalUserService,
    @Value("\${mordor.secret}") private val secret: String,
    @Value("\${mordor.ldap.url:}") private val ldapUrl: String,
    @Value("\${mordor.ldap.user.base:}") private val userBase: String,
    @Value("\${mordor.ldap.user.filter:}") private val userFilter: String,
    @Value("\${mordor.ldap.use_krb_auth:false}") private val krbAuth: Boolean,
    private val ldapRolePopulator: LdapRolePopulator
) : WebSecurityConfigurerAdapter() {
    private inner class DelegatingUserDetailService : UserDetailsService {
        override fun loadUserByUsername(username: String): UserDetails {
            return try {
                userService.loadUserByUsername(username)
            } catch (ex: UsernameNotFoundException) {
                if (ldapUrl.isNotEmpty()) {
                    ldapDetailsService().loadUserByUsername(username)
                } else {
                    throw ex
                }
            }
        }
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests().antMatchers("/", "/register/**").permitAll()
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login/").permitAll()
            .and().logout().logoutUrl("/logout/").permitAll()
            .and().rememberMe().key(secret).tokenRepository(tokenRepository)
            .userDetailsService(DelegatingUserDetailService())
            .and().exceptionHandling().accessDeniedPage("/403/")
    }

    override fun configure(web: WebSecurity) {
        web.ignoring()
            .antMatchers("/static/**")
    }

    fun ldapDetailsService(): UserDetailsService {
        val ldapContextSource = DefaultSpringSecurityContextSource(ldapUrl)
        ldapContextSource.afterPropertiesSet()
        val userSearch = FilterBasedLdapUserSearch(userBase, userFilter, ldapContextSource)
        val ldapUserService = LdapUserDetailsService(userSearch, ldapRolePopulator)
        return UserDetailsService {
            ldapUserService.loadUserByUsername(it.substringBefore('@'))
        }
    }

    fun kerberosAuthenticationProvider(): KerberosAuthenticationProvider {
        val provider = KerberosAuthenticationProvider()
        val client = SunJaasKerberosClient()
        provider.setKerberosClient(client)
        provider.setUserDetailsService(ldapDetailsService())
        return provider
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService)

        if (ldapUrl.isNotEmpty()) {
            if (krbAuth) {
                auth.authenticationProvider(kerberosAuthenticationProvider())
            } else {
                auth.ldapAuthentication()
                    .userSearchBase(userBase)
                    .userSearchFilter(userFilter)
                    .contextSource().url(ldapUrl)
                    .and().ldapAuthoritiesPopulator(ldapRolePopulator)
            }
        }
    }
}
