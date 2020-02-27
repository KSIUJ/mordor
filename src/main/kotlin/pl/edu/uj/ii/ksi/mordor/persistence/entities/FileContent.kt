package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.*

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
