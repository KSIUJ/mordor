package pl.edu.uj.ii.ksi.mordor.persistence.managers

import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileThumbnail
import java.io.File

interface MetadataManager {
    fun extractAndSaveMetadata(file: File) : Metadata

    fun getAllEntries(metadata: Metadata) : Set<FileEntry>
    // TODO: QUERIES
    fun anyEntries(metadata: Metadata) : Boolean

    fun removeMetadata(metadata: Metadata)

    fun getFileContent(metadata: Metadata) : FileContent

    fun getFileThumbnail(metadata: Metadata) : FileThumbnail
}