package pl.edu.uj.ii.ksi.mordor.services.upload.session

import java.time.LocalDateTime
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User

data class FileUploadSession(
    val id: String,
    val user: User,
    val creationDate: LocalDateTime
)
