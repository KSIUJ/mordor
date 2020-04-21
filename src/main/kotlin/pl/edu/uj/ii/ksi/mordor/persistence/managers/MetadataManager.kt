package pl.edu.uj.ii.ksi.mordor.persistence.managers

import java.io.File
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileThumbnail

interface MetadataManager {
    fun extractAndSaveMetadata(file: File): FileMetadata

    fun getAllEntries(metadata: FileMetadata): Array<FileEntry>
    // TODO: QUERIES
    fun anyEntries(metadata: FileMetadata): Boolean

    fun removeMetadata(metadata: FileMetadata)

    fun getFileContent(metadata: FileMetadata): FileContent

    fun getFileThumbnail(metadata: FileMetadata): FileThumbnail
}
