package pl.edu.uj.ii.ksi.mordor.models.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.models.entities.RememberMeToken

interface RememberMeTokenRepository : JpaRepository<RememberMeToken, String> {
    fun findByUser_userName(userName: String): List<RememberMeToken>
}
