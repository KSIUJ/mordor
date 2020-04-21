package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class FileEntry(
    @Id
    var path: String,

    @ManyToOne(optional = false, cascade = [CascadeType.DETACH])
    var metadata: FileMetadata,

    @Enumerated(EnumType.STRING)
    var status: FileEntryStatus
)
