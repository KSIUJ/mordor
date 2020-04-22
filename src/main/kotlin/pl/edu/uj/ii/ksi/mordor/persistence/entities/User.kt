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
    var userName: String = "",

    var password: String? = null,

    @Column(unique = true)
    var email: String? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var enabled: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.NOBODY
)
