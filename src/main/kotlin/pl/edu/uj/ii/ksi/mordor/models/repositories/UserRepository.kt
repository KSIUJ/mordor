package pl.edu.uj.ii.ksi.mordor.models.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.models.entities.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByUserName(username: String): User?
}
