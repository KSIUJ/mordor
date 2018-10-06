package pl.edu.uj.ii.ksi.mordor.models.entities

import javax.persistence.*

@Entity
data class FileMetadata(
        @Id
        var fileHash: String?,

        var title: String?,

        var author: String?,

        var description: String?,

        var mimeType: String?,

        @Suppress("ArrayInDataClass")
        @Column(length = 200 * 1024)
        var thumbnail: ByteArray?,

        @OneToMany(orphanRemoval = true)
        var files: List<FileEntry>?
)
