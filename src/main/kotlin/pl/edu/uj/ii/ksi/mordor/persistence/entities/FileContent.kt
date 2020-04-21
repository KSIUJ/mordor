package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
data class FileContent(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @OneToOne(optional = false, cascade = [CascadeType.DETACH])
    var file: FileMetadata?,

    @Column(length = 200 * 1024)
    var text: String?
)
