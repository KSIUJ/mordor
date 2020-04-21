package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class FileThumbnail(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @Column(unique = true)
    var path: String,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "thumbnail")
    var metadata: Set<FileMetadata>
)
