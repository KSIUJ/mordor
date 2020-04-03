package pl.edu.uj.ii.ksi.mordor.persistence.managers

import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileThumbnail
import java.io.File

interface FileManager {
    fun addFile(file: File,
                path: String,
                metadata: Metadata? = null,
                content: FileContent? = null,
                thumbnail: FileThumbnail? = null)

    fun getFile(path: String) : File
    fun getFile(metadata: Metadata) : File

    fun doesFileExists(path: String) : Boolean

    fun removeFile(path: String)
    fun removeAllFilesLike(file: File)
    fun removeAllFilesLike(path: String)

    fun getFileContent(path: String) : FileContent
    fun getFileThumbnail(path: String) : FileThumbnail
}