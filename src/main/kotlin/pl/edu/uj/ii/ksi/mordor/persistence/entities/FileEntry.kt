package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.*

@Entity
data class FileEntry(
    @Id
    var path: String,

    @ManyToOne(optional = false, cascade = [CascadeType.DETACH])
    var metadata: FileMetadata
)
