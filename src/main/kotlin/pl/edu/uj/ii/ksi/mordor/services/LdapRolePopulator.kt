package pl.edu.uj.ii.ksi.mordor.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role

@Service
class LdapRolePopulator(
    @Value("\${mordor.ldap.role.admin:}") private val ldapAdminRole: String,
    @Value("\${mordor.ldap.role.mod:}") private val ldapModRole: String
) : LdapAuthoritiesPopulator {
    companion object {
        private val logger = LoggerFactory.getLogger(LdapRolePopulator::class.java)
    }

    override fun getGrantedAuthorities(userData: DirContextOperations, username: String): Collection<GrantedAuthority> {
        val groups = userData.getStringAttributes("memberOf")?.toSet()

        return when {
            groups == null -> {
                logger.info("No LDAP groups retrieved for $username - no memberOf attribute")
                return Role.ROLE_USER.permissions
            }
            ldapAdminRole.isNotEmpty() && groups.contains(ldapAdminRole) -> Role.ROLE_ADMIN.permissions
            ldapModRole.isNotEmpty() && groups.contains(ldapModRole) -> Role.ROLE_MOD.permissions
            else -> Role.ROLE_USER.permissions
        }
    }
}
