package pl.edu.uj.ii.ksi.mordor.services.repository

import java.io.File
import java.io.IOException
import java.nio.file.Files
import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory

class RepositoryFile(
    override val name: String,
    override val relativePath: String,
    val file: File,
    val title: String?,
    val author: String?,
    val description: String?,
    private var mime: String?,
    val thumbnail: String?
) : RepositoryEntity {
    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryFile::class.java)

        private val safeMimetype = setOf(
            "application/pdf",
            "image/png", "image/jpeg", "image/jpeg",
            "application/octet-stream"
            // TODO: expand
        )

        private val codeFileExts = setOf(
            "sh", "bash", "bat", "conf", "config", "cfg", "coffee", "h", "hxx", "cpp", "c", "cxx", "cs", "css", "diff",
            "ini", "toml", "java", "js", "json", "md", "m", "mm", "pl", "pm", "t", "php", "properties", "py", "rb",
            "sql", "html", "xml", "asm", "s", "ldif", "yaml", "less", "scss", "scala", "ml", "mli", "re", "rei", "hs",
            "lhs", "fs", "erl", "hrl", "ex", "clj", "hy", "lisp", "lsp", "scm", "asciidoc", "adoc", "asc", "tex",
            "proto", "protobuf", "f", "f90", "nb", "mat", "r", "as", "fla", "dart", "gml", "hsp", "ls", "lua", "moon",
            "qml", "ts", "vbs", "go", "rs", "swift", "ada", "pde", "awk", "b", "bas", "d", "gradle", "groovy", "julia",
            "kt", "nim"
            // TODO: expand
        )

        private val codeFileNames = setOf(
            "Makefile", "Dockerfile", "CMakeLists.txt"
            // TODO: expand
        )
    }

    val mimeType: String
        get() {
            return mime ?: try {
                mime = (Files.probeContentType(file.toPath()) ?: "application/octet-stream")
                mime!!
            } catch (ex: IOException) {
                logger.error("Unable to get content type for $relativePath", ex)
                "application/octet-stream"
            }
        }

    val isCode: Boolean
        get() {
            val name = name.toLowerCase()
            return codeFileNames.contains(name) || codeFileExts.contains(FilenameUtils.getExtension(name))
        }

    val browserSafeMimeType: String
        get() {
            val mime = mimeType
            return if (safeMimetype.contains(mime)) mime else "application/octet-stream"
        }
}
