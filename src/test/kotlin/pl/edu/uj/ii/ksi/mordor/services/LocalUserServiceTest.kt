package pl.edu.uj.ii.ksi.mordor.services

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test

import org.junit.Assert.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository

class LocalUserServiceTest {
    val USER1 = User(0, "jsmith", "passwd", "jsmith@example.com", "John",
            "Smith", true, Role.ROLE_USER)
    val mockUserRepository: UserRepository = mock {
        on { findByUserName("jsmith") } doReturn USER1
    }

    val localUserService = LocalUserService(mockUserRepository)

    @Test
    fun loadUserByUsername_found() {
        val userDetails = localUserService.loadUserByUsername(USER1.userName)
        assertEquals(USER1.userName, userDetails.username)
        assertEquals(USER1.password, userDetails.password)
        assertEquals(USER1.role.permissions, userDetails.authorities)
        assertEquals(USER1.enabled, userDetails.isEnabled)
        assertEquals(true, userDetails.isAccountNonExpired)
        assertEquals(true, userDetails.isCredentialsNonExpired)
        assertEquals(true, userDetails.isAccountNonLocked)
    }

    @Test(expected = UsernameNotFoundException::class)
    fun loadUserByUsername_unknown() {
        localUserService.loadUserByUsername("unknownUser")
    }
}
