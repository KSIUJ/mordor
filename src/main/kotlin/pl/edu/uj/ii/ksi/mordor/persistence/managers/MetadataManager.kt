package pl.edu.uj.ii.ksi.mordor.persistence.managers

import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileThumbnail
import java.io.File

interface MetadataManager {
    fun getMetadata(file : File) : Metadata
    fun getMetadata(path : String) : Metadata
    fun getMetadata(id : Long?) : Metadata

    fun extractMetadata(file: File) : Metadata
    fun extractFileContent(file: File) : FileContent
    fun extractFileThumbnail(file: File) : FileThumbnail

    fun getAllPathsOf(metadata: Metadata) : Set<String>

    fun anyPaths(metadata: Metadata) : Boolean

    fun removeMetadata(metadata: Metadata)

    fun getFileContent(id: Long?) : FileContent
    fun getFileThumbnail(id: Long) : FileThumbnail
}