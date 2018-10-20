package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByUserName(username: String): User?

    fun findByEmail(email: String): User?
}
