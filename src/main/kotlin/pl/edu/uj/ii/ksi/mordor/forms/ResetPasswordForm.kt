package pl.edu.uj.ii.ksi.mordor.forms

import javax.validation.constraints.Size

data class ResetPasswordForm(
    val token: String = "",

    @get:Size(min = 8, max = 256)
    val password: String = "",

    val password2: String = ""
)
