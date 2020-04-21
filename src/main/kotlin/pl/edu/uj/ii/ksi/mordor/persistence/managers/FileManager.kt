package pl.edu.uj.ii.ksi.mordor.persistence.managers

import java.io.File
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry

interface FileManager {
    fun addFile(file: File, path: String)

    fun createDirectory(path: String)

    fun getFileEntry(path: String): FileEntry?

    fun approveFile(path: String)

    fun rejectFile(path: String)

    fun removeEntry(path: String)
}
