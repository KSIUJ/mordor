package pl.edu.uj.ii.ksi.mordor.services.upload.session

import pl.edu.uj.ii.ksi.mordor.persistence.entities.User

data class FileUploadSession(
    var id: String,

    var user: User
)
