package pl.edu.uj.ii.ksi.mordor.services

import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role

data class ExternalUser(
    var userName: String,

    var email: String?,

    var firstName: String?,

    var lastName: String?,

    var role: Role = Role.NOBODY
)
