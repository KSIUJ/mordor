package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne

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
