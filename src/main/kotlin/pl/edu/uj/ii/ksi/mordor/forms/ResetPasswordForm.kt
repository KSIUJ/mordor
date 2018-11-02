package pl.edu.uj.ii.ksi.mordor.forms

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class ResetPasswordForm(
    @get:NotEmpty
    val token: String = "",

    @get:NotEmpty
    @get:Size(min = 8, max = 256)
    val password: String = "",

    @get:NotEmpty
    val password2: String = ""
)
