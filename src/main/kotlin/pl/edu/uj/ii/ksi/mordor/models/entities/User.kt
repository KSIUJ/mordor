package pl.edu.uj.ii.ksi.mordor.models.entities

import javax.persistence.*

@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,

        @Column(nullable = false, unique = true)
        var username: String,

        @Column(nullable = false)
        var password: String,

        var email: String?,

        var firstName: String?,

        var lastName: String?,

        var enabled: Boolean = false,

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var role: Role = Role.ROLE_NOBODY
) {
    // Empty constructor for JPA
    constructor() : this(null, "", "", null, null, null, false, Role.ROLE_NOBODY)
}
