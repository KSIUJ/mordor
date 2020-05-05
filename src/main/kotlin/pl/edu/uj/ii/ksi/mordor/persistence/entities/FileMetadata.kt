package pl.edu.uj.ii.ksi.mordor.persistence.entities

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity
data class FileMetadata(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var fileHash: String?,

    var title: String?,

    var author: String?,

    var description: String?,

    var mimeType: String?,

    @OneToOne(mappedBy = "file", orphanRemoval = true, cascade = [CascadeType.REMOVE], fetch = FetchType.LAZY)
    var thumbnail: FileThumbnail?,

    @OneToOne(mappedBy = "file", orphanRemoval = true, cascade = [CascadeType.REMOVE], fetch = FetchType.LAZY)
    var crawledContent: FileContent?,

    @OneToMany(mappedBy = "metadata", orphanRemoval = true)
    var files: List<FileEntry>? = mutableListOf()
) {
    override fun toString(): String { return "${this.javaClass.simpleName}-$id" }
}
