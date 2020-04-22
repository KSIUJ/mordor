package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class FileEntry(
    @Id
    var path: String,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    var metadata: FileMetadata,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    var session: FileUploadSession
)
