package pl.edu.uj.ii.ksi.mordor.forms

import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role

data class UserForm(
    val id: Long,

    val userName: String,

    val email: String?,

    val password: String?,

    val password2: String?,

    val firstName: String?,

    val lastName: String?,

    val enabled: Boolean,

    val role: Role
)
