package pl.edu.uj.ii.ksi.mordor.persistence.entities

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType

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

    @ManyToOne(optional = false)
    var user: User?
) {
    fun isValid(): Boolean {
        return expireData!!.after(Date())
    }
}
