package pl.edu.uj.ii.ksi.mordor.persistence.managers

import java.io.File
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata

interface FileManager {
    fun addFile(file: File, path: String)

    fun createDirectory(path: String)

    fun getFile(path: String): File
    fun getFile(metadata: FileMetadata): File

    fun getFileEntry(path: String): FileEntry?

    fun approveFile(path: String)

    fun rejectFile(path: String)

    fun removeFile(path: String)
}
