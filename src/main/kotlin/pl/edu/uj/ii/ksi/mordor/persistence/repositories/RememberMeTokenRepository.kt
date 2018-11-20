package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.RememberMeToken

interface RememberMeTokenRepository : JpaRepository<RememberMeToken, String> {
    fun findByUserName(userName: String): List<RememberMeToken>
}
