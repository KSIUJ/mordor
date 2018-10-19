package pl.edu.uj.ii.ksi.mordor.models.entities

import javax.persistence.*

@Entity
data class FileContent(
        @Id
        var id: Long? = null,

        @MapsId
        @OneToOne(optional = false)
        var file: FileMetadata?,

        @Column(length = 200 * 1024)
        var text: String?
)
