package pl.edu.uj.ii.ksi.mordor.models.entities

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import java.util.*
import javax.persistence.*

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
    constructor() : this(null, null, null, null)

    fun getToken(): PersistentRememberMeToken {
        return PersistentRememberMeToken(user?.userName, series, value, date)
    }
}
