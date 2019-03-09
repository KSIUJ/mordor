package pl.edu.uj.ii.ksi.mordor.configuration

import java.net.URLEncoder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSessionEvent
import org.jasig.cas.client.session.SingleSignOutFilter
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role
import pl.edu.uj.ii.ksi.mordor.services.ExternalUser
import pl.edu.uj.ii.ksi.mordor.services.ExternalUserService

@Configuration
class CasConfig(
    @Value("\${mordor.site.address:}") private val siteUrl: String,
    @Value("\${mordor.cas.url:}") private val casUrl: String,
    @Value("\${mordor.cas.role.attribute:}") private val casRoleAttribute: String,
    @Value("\${mordor.cas.role.admin:}") private val casAdmin: String,
    @Value("\${mordor.cas.role.mod:}") private val casMod: String,
    private val externalUserService: ExternalUserService
) {
    fun casEnabled(): Boolean {
        return casUrl.isNotBlank()
    }

    @Bean
    fun serviceProperties(): ServiceProperties {
        val appLogin = "$siteUrl/cas"
        val serviceProperties = ServiceProperties()
        serviceProperties.service = appLogin
        serviceProperties.isAuthenticateAllArtifacts = true
        return serviceProperties
    }

    fun casAuthenticationEntryPoint(): AuthenticationEntryPoint {
        val entryPoint = object : CasAuthenticationEntryPoint() {
            override fun createServiceUrl(request: HttpServletRequest?, response: HttpServletResponse?): String {
                return this.serviceProperties.service + "?redirect=" + URLEncoder.encode(request!!.requestURI, "UTF-8")
            }
        }
        entryPoint.loginUrl = "$casUrl/login"
        entryPoint.serviceProperties = serviceProperties()
        return entryPoint
    }

    @Bean
    fun ticketValidatorCas30(): Cas30ServiceTicketValidator {
        return Cas30ServiceTicketValidator(casUrl)
    }

    private inner class CasUserDetailsService : AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
        private fun getStrOrNull(map: Map<String, Any?>, key: String): String? {
            return map.getOrDefault(key, null) as? String
        }

        override fun loadUserDetails(token: CasAssertionAuthenticationToken): UserDetails {
            val attrs = token.assertion.principal.attributes

            var role = Role.USER

            if (casRoleAttribute.isNotBlank() && attrs.containsKey(casRoleAttribute)) {
                val groups = attrs[casRoleAttribute] as? List<*> ?: listOf(attrs[casRoleAttribute] as? String)
                when {
                    groups.contains(casAdmin) -> role = Role.ADMIN
                    groups.contains(casMod) -> role = Role.MOD
                }
            }

            val externalUser = ExternalUser(token.name, getStrOrNull(attrs, "mail"),
                getStrOrNull(attrs, "givenName"), getStrOrNull(attrs, "sn"), role)
            return User(token.name, "external", true, true, true, true,
                externalUserService.loginExternalAccount(externalUser).permissions)
        }
    }

    fun casAuthenticationProvider(): CasAuthenticationProvider {
        val provider = CasAuthenticationProvider()
        provider.setServiceProperties(serviceProperties())
        provider.setTicketValidator(ticketValidatorCas30())
        provider.setAuthenticationUserDetailsService(CasUserDetailsService())
        provider.setKey("MORDOR_CAS")
        return provider
    }

    @EventListener
    fun singleSignOutHttpSessionListener(event: HttpSessionEvent): SingleSignOutHttpSessionListener {
        return SingleSignOutHttpSessionListener()
    }

    fun singleSignOutFilter(): SingleSignOutFilter {
        val singleSignOutFilter = SingleSignOutFilter()
        singleSignOutFilter.setCasServerUrlPrefix(casUrl)
        singleSignOutFilter.setIgnoreInitConfiguration(true)
        return singleSignOutFilter
    }

    @Bean
    fun securityContextLogoutHandler(): SecurityContextLogoutHandler {
        return SecurityContextLogoutHandler()
    }

    fun logoutFilter(): LogoutFilter {
        val logoutFilter = LogoutFilter(
            "$casUrl/logout",
            securityContextLogoutHandler())
        logoutFilter.setFilterProcessesUrl("/logout/cas")
        return logoutFilter
    }
}
