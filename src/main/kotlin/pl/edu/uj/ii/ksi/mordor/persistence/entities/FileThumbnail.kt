package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.*

@Entity
data class FileThumbnail(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @OneToOne(optional = false, cascade = [CascadeType.DETACH])
    var file: FileMetadata?,

    @Suppress("ArrayInDataClass")
    @Column(length = 200 * 1024)
    var thumbnail: ByteArray?
)
