package pl.edu.uj.ii.ksi.mordor.services

import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.ldap.core.DirContextOperations
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role

class LdapRolePopulatorTest {
    companion object {
        private const val ldapAdminRole = "cn=admins,cn=groups,cn=accounts,dc=ksi,dc=ii,dc=uj,dc=edu,dc=pl"
        private const val ldapModRole = "cn=mordor,cn=groups,cn=accounts,dc=ksi,dc=ii,dc=uj,dc=edu,dc=pl"
        private const val testUser = "user"
    }

    private val ldapRolePopulator = LdapRolePopulator(ldapAdminRole, ldapModRole)

    @Test
    fun getGrantedAuthorities_admin() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttributes("memberOf") }.thenReturn(arrayOf(ldapAdminRole, ldapModRole))
        }
        val roles = ldapRolePopulator.getGrantedAuthorities(dirContextOperations, testUser)
        assertThat(roles.containsAll(Role.ROLE_ADMIN.permissions), equalTo(true))
        assertThat(roles.size, equalTo(Role.ROLE_ADMIN.permissions.size))
    }

    @Test
    fun getGrantedAuthorities_mod() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttributes("memberOf") }.thenReturn(arrayOf(ldapModRole))
        }
        val roles = ldapRolePopulator.getGrantedAuthorities(dirContextOperations, testUser)
        assertThat(roles.containsAll(Role.ROLE_MOD.permissions), equalTo(true))
        assertThat(roles.size, equalTo(Role.ROLE_MOD.permissions.size))
    }

    @Test
    fun getGrantedAuthorities_user() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttributes("memberOf") }.thenReturn(arrayOf())
        }
        val roles = ldapRolePopulator.getGrantedAuthorities(dirContextOperations, testUser)
        assertThat(roles.containsAll(Role.ROLE_USER.permissions), equalTo(true))
        assertThat(roles.size, equalTo(Role.ROLE_USER.permissions.size))
    }
}
