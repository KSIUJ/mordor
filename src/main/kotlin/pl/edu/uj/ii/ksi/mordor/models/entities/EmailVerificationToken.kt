package pl.edu.uj.ii.ksi.mordor.models.entities

import java.util.*
import javax.persistence.*

@Entity
data class EmailVerificationToken(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long?,

        @Column(unique = true, nullable = false)
        var token: String?,

        @Column(nullable = false)
        @Temporal(TemporalType.TIMESTAMP)
        var expireData: Date?,

        @OneToOne(optional = false)
        var user: User?
) {
    constructor() : this(null, null, null, null)

    fun isValid(): Boolean {
        return expireData!!.after(Date())
    }
}
