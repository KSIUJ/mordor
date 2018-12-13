package pl.edu.uj.ii.ksi.mordor.services

import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.springframework.ldap.core.DirContextOperations
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role

class LdapAttributesMapperTest {
    companion object {
        private const val ldapUserMemberOf: String = "memberOf"
        private const val ldapUserFirstName: String = "givenName"
        private const val ldapUserLastName: String = "sn"
        private const val ldapUserGecos: String = "gecos"
        private const val ldapUserEmail: String = "mail"
        private const val ldapAdminRole: String = "cn=admins,cn=groups,cn=accounts,dc=ksi,dc=ii,dc=uj,dc=edu,dc=pl"
        private const val ldapModRole: String = "cn=mordor,cn=groups,cn=accounts,dc=ksi,dc=ii,dc=uj,dc=edu,dc=pl"
        private const val testUser = "user"
    }

    private val ldapAttributesMapper = LdapAttributesMapper(ldapUserMemberOf, ldapUserFirstName, ldapUserLastName,
        "", ldapUserEmail, ldapAdminRole, ldapModRole)

    @Test
    fun getRole_admin() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttributes(ldapUserMemberOf) }.thenReturn(arrayOf(ldapAdminRole, ldapModRole))
        }
        val role = ldapAttributesMapper.getRole(dirContextOperations, testUser)
        assertThat(role, equalTo(Role.ADMIN))
    }

    @Test
    fun getRole_mod() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttributes(ldapUserMemberOf) }.thenReturn(arrayOf(ldapModRole))
        }
        val role = ldapAttributesMapper.getRole(dirContextOperations, testUser)
        assertThat(role, equalTo(Role.MOD))
    }

    @Test
    fun getRole_user() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttributes(ldapUserMemberOf) }.thenReturn(arrayOf())
        }
        val role = ldapAttributesMapper.getRole(dirContextOperations, testUser)
        assertThat(role, equalTo(Role.USER))
    }

    @Test
    fun getRole_empty() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttributes(ldapUserMemberOf) }.thenReturn(null)
        }
        val role = ldapAttributesMapper.getRole(dirContextOperations, testUser)
        assertThat(role, equalTo(Role.USER))
    }

    @Test
    fun getEmail_empty() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttribute(ldapUserEmail) }.thenReturn(null)
        }
        val mail = ldapAttributesMapper.getEmail(dirContextOperations, testUser)
        assertThat(mail, equalTo<String?>(null))
    }

    @Test
    fun getEmail_exists() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttribute(ldapUserEmail) }.thenReturn("user@example.com")
        }
        val mail = ldapAttributesMapper.getEmail(dirContextOperations, testUser)
        assertThat(mail, equalTo("user@example.com"))
    }

    @Test
    fun getFirstName_null() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttribute(ldapUserFirstName) }.thenReturn(null)
        }
        val name = ldapAttributesMapper.getFirstName(dirContextOperations, testUser)
        assertThat(name, equalTo<String?>(null))
    }

    @Test
    fun getFirstName_exists() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttribute(ldapUserFirstName) }.thenReturn("John")
        }
        val name = ldapAttributesMapper.getFirstName(dirContextOperations, testUser)
        assertThat(name, equalTo("John"))
    }

    @Test
    fun getLastName_null() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttribute(ldapUserLastName) }.thenReturn(null)
        }
        val name = ldapAttributesMapper.getLastName(dirContextOperations, testUser)
        assertThat(name, equalTo<String?>(null))
    }

    @Test
    fun getLastName_exists() {
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttribute(ldapUserLastName) }.thenReturn("Smith")
        }
        val name = ldapAttributesMapper.getLastName(dirContextOperations, testUser)
        assertThat(name, equalTo("Smith"))
    }

    @Test
    fun getFirstLastNameGecos_null() {
        val ldapAttributesMapper = LdapAttributesMapper(ldapUserMemberOf, "", "",
            ldapUserGecos, ldapUserEmail, ldapAdminRole, ldapModRole)
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttribute(ldapUserGecos) }.thenReturn(null)
        }
        val name = ldapAttributesMapper.getFirstName(dirContextOperations, testUser)
        assertThat(name, equalTo<String?>(null))
        val lastName = ldapAttributesMapper.getLastName(dirContextOperations, testUser)
        assertThat(lastName, equalTo<String?>(null))
    }

    @Test
    fun getFirstLastNameGecos_exist() {
        val ldapAttributesMapper = LdapAttributesMapper(ldapUserMemberOf, "", "",
            ldapUserGecos, ldapUserEmail, ldapAdminRole, ldapModRole)
        val dirContextOperations: DirContextOperations = mock {
            on { getStringAttribute(ldapUserGecos) }.thenReturn("John Paul II,Vatican,,,,")
        }
        val name = ldapAttributesMapper.getFirstName(dirContextOperations, testUser)
        assertThat(name, equalTo<String?>("John Paul"))
        val lastName = ldapAttributesMapper.getLastName(dirContextOperations, testUser)
        assertThat(lastName, equalTo<String?>("II"))
    }
}
