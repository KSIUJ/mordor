package pl.edu.uj.ii.ksi.mordor.models.entities

import javax.persistence.*

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

        @OneToOne(mappedBy = "file", orphanRemoval = true, cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
        var thumbnail: FileThumbnail?,

        @OneToOne(mappedBy = "file", orphanRemoval = true, cascade = [CascadeType.REMOVE], fetch = FetchType.LAZY)
        var content: FileContent?,

        @OneToMany(mappedBy = "metadata", orphanRemoval = true)
        var files: List<FileEntry>?
)
