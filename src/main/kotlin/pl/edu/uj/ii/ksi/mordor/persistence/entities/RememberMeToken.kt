package pl.edu.uj.ii.ksi.mordor.persistence.entities

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken

@Entity
data class RememberMeToken(
    @Id
    var series: String?,

    @Column(nullable = false)
    var value: String?,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    var date: Date?,

    @OneToOne(optional = false)
    var user: User?
) {
    fun getToken(): PersistentRememberMeToken {
        return PersistentRememberMeToken(user?.userName, series, value, date)
    }
}
