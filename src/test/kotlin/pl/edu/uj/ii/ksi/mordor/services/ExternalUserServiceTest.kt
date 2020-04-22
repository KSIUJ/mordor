package pl.edu.uj.ii.ksi.mordor.services

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentCaptor
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository

class ExternalUserServiceTest {
    companion object {
        private val externalUser = ExternalUser("test", "test@example.com", "Test", "User", Role.ADMIN)
        private val localUser = User(0, externalUser.userName, null, "test2@example.com",
            "Test2", "User2", false, Role.EXTERNAL, ArrayList())
        private val overrideUser = User(0, externalUser.userName, null, "test2@example.com",
            "Test2", "User2", false, Role.MOD, ArrayList())
    }

    @Test
    fun loginExternalAccount_new() {
        val userRepository: UserRepository = mock {}
        val service = ExternalUserService(userRepository)
        val role = service.loginExternalAccount(externalUser)
        assertThat(role, equalTo(Role.ADMIN))
        verify(userRepository).findByUserName(any())
        val captor = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captor.capture())
        assertThat(captor.value.userName, equalTo(externalUser.userName))
        assertThat(captor.value.firstName, equalTo(externalUser.firstName))
        assertThat(captor.value.lastName, equalTo(externalUser.lastName))
        assertThat(captor.value.email, equalTo(externalUser.email))
        assertThat(captor.value.role, equalTo(Role.EXTERNAL))
    }

    @Test
    fun loginExternalAccount_existing() {
        val userRepository: UserRepository = mock {
            on { findByUserName(externalUser.userName) } doReturn localUser
        }
        val service = ExternalUserService(userRepository)
        val role = service.loginExternalAccount(externalUser)
        assertThat(role, equalTo(Role.ADMIN))
        verify(userRepository).findByUserName(any())
        val captor = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captor.capture())
        assertThat(captor.value.userName, equalTo(externalUser.userName))
        assertThat(captor.value.firstName, equalTo(externalUser.firstName))
        assertThat(captor.value.lastName, equalTo(externalUser.lastName))
        assertThat(captor.value.email, equalTo(externalUser.email))
        assertThat(captor.value.role, equalTo(localUser.role))
    }

    @Test
    fun loginExternalAccount_override() {
        val userRepository: UserRepository = mock {
            on { findByUserName(externalUser.userName) } doReturn overrideUser
        }
        val userService = ExternalUserService(userRepository)
        val role = userService.loginExternalAccount(externalUser)
        assertThat(role, equalTo(Role.MOD))
        verify(userRepository).findByUserName(any())
        val captor = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captor.capture())
        assertThat(captor.value.userName, equalTo(externalUser.userName))
        assertThat(captor.value.firstName, equalTo(externalUser.firstName))
        assertThat(captor.value.lastName, equalTo(externalUser.lastName))
        assertThat(captor.value.email, equalTo(externalUser.email))
        assertThat(captor.value.role, equalTo(overrideUser.role))
    }
}
