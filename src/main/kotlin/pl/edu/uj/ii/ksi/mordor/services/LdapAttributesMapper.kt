package pl.edu.uj.ii.ksi.mordor.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.ldap.core.DirContextOperations
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role

@Service
class LdapAttributesMapper(
    @Value("\${mordor.ldap.user.member_of:}") private val ldapUserMemberOf: String,
    @Value("\${mordor.ldap.user.given_name:}") private val ldapUserFirstName: String,
    @Value("\${mordor.ldap.user.sn:}") private val ldapUserLastName: String,
    @Value("\${mordor.ldap.user.gecos:}") private val ldapUserGecos: String,
    @Value("\${mordor.ldap.user.email:}") private val ldapUserEmail: String,
    @Value("\${mordor.ldap.role.admin:}") private val ldapAdminRole: String,
    @Value("\${mordor.ldap.role.mod:}") private val ldapModRole: String
) {
    companion object {
        private val logger = LoggerFactory.getLogger(LdapAttributesMapper::class.java)
    }

    fun getFirstName(userData: DirContextOperations, username: String): String? {
        if (ldapUserFirstName.isNotEmpty()) {
            val fname = userData.getStringAttribute(ldapUserFirstName)
            if (fname == null) {
                logger.info("No LDAP first name retrieved for $username - no $ldapUserFirstName attribute")
            }
            return fname
        } else if (ldapUserGecos.isNotEmpty()) {
            val gecos = userData.getStringAttribute(ldapUserGecos)
            return if (gecos == null) {
                logger.info("No LDAP first name retrieved for $username - no $ldapUserGecos attribute")
                null
            } else {
                val names = gecos.split(",")[0].split(" ")
                names.subList(0, names.size - 1).joinToString(" ")
            }
        }
        return null
    }

    fun getLastName(userData: DirContextOperations, username: String): String? {
        if (ldapUserLastName.isNotEmpty()) {
            val lname = userData.getStringAttribute(ldapUserLastName)
            if (lname == null) {
                logger.info("No LDAP last name retrieved for $username - no $ldapUserLastName attribute")
            }
            return lname
        } else if (ldapUserGecos.isNotEmpty()) {
            val gecos = userData.getStringAttribute(ldapUserGecos)
            return if (gecos == null) {
                logger.info("No LDAP last name retrieved for $username - no $ldapUserGecos attribute")
                null
            } else {
                gecos.split(",")[0].split(" ").last()
            }
        }
        return null
    }

    fun getEmail(userData: DirContextOperations, username: String): String? {
        if (ldapUserEmail.isNotEmpty()) {
            val email = userData.getStringAttribute(ldapUserEmail)
            if (email == null) {
                logger.info("No LDAP email retrieved for $username - no $ldapUserEmail attribute")
            }
            return email
        }
        return null
    }

    fun getRole(userData: DirContextOperations, username: String): Role {
        if (ldapUserMemberOf.isNotEmpty()) {
            val groups = userData.getStringAttributes(ldapUserMemberOf)?.toSet()

            return when {
                groups == null -> {
                    logger.info("No LDAP groups retrieved for $username - no $ldapUserMemberOf attribute")
                    return Role.USER
                }
                ldapAdminRole.isNotEmpty() && groups.contains(ldapAdminRole) -> Role.ADMIN
                ldapModRole.isNotEmpty() && groups.contains(ldapModRole) -> Role.MOD
                else -> Role.USER
            }
        } else {
            return Role.USER
        }
    }

    fun getExternalUser(userData: DirContextOperations, username: String): ExternalUser {
        return ExternalUser(username,
            getEmail(userData, username),
            getFirstName(userData, username),
            getLastName(userData, username),
            getRole(userData, username)
        )
    }
}
