package pl.edu.uj.ii.ksi.mordor.services.repository

import java.io.File
import java.io.InputStream

data class RepositoryFile(
    override val name: String,
    override val relativePath: String,
    private val file: File,
    val title: String?,
    val author: String?,
    val description: String?,
    val mimeType: String?,
    val thumbnail: String?
) : RepositoryEntity {
    fun newInputStream(): InputStream {
        return file.inputStream()
    }
}
