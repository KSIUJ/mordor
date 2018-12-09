package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var userName: String,

    var password: String?,

    @Column(unique = true)
    var email: String?,

    var firstName: String?,

    var lastName: String?,

    var enabled: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.NOBODY
) {
    constructor() : this(null, "", "", null, null, null, false, Role.NOBODY)
}
