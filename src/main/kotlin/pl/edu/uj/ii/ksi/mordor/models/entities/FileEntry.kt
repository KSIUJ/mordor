package pl.edu.uj.ii.ksi.mordor.models.entities

import javax.persistence.*

@Entity
data class FileEntry (
        @Id
        var path: String,

        @ManyToOne(fetch = FetchType.EAGER, optional = false)
        var metadata: FileMetadata
)
