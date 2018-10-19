package pl.edu.uj.ii.ksi.mordor.models.entities

import javax.persistence.*

@Entity
data class FileThumbnail(
        @Id
        var id: Long? = null,

        @MapsId
        @OneToOne(optional = false)
        var file: FileMetadata?,

        @Suppress("ArrayInDataClass")
        @Column(length = 200 * 1024)
        var thumbnail: ByteArray?
)
