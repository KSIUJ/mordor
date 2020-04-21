package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class FileMetadata(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var fileHash: String?,

    var title: String?,

    var author: String?,

    var description: String?,

    var mimeType: String?,

    @ManyToOne(optional = false, cascade = [CascadeType.DETACH])
    var thumbnail: FileThumbnail?
)
