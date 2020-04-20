package pl.edu.uj.ii.ksi.mordor.persistence.managers

import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import java.io.File

interface FileManager {
    fun addFile(file: File, path: String)

    fun createDirectory(path: String)

    fun getFile(path: String) : File
    fun getFile(metadata: Metadata) : File

    fun getFileEntry(path: String) : FileEntry?

    fun approveFile(path: String)

    fun rejectFile(path: String)

    fun removeFile(path: String)
}