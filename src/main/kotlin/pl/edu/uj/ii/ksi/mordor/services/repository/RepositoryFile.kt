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

        private val displayableImageTypes = setOf(
            // Based on: https://en.wikipedia.org/wiki/Comparison_of_web_browsers#Image_format_support
            "image/png", "image/jpeg", "image/gif", "image/svg+xml"
        )

        private val safeMimetype = setOf(
            "application/pdf",
            "application/octet-stream"
            // TODO: expand
        ) + displayableImageTypes

        private val codeFileExts = setOf(
            "sh", "bash", "bat", "conf", "config", "cfg", "coffee", "h", "hxx", "cpp", "c", "cxx", "cs", "css", "diff",
            "ini", "toml", "java", "js", "json", "md", "m", "mm", "pl", "pm", "t", "php", "properties", "py", "rb",
            "sql", "html", "xml", "asm", "s", "ldif", "yaml", "less", "scss", "scala", "ml", "mli", "re", "rei", "hs",
            "lhs", "fs", "erl", "hrl", "ex", "clj", "hy", "lisp", "lsp", "scm", "asciidoc", "adoc", "asc", "tex",
            "proto", "protobuf", "f", "f90", "nb", "mat", "r", "as", "fla", "dart", "gml", "hsp", "ls", "lua", "moon",
            "qml", "ts", "vbs", "go", "rs", "swift", "ada", "pde", "awk", "b", "bas", "d", "gradle", "groovy", "julia",
            "kt", "nim", "in", "out"
            // TODO: expand
        )

        private val codeFileNames = setOf(
            "Makefile", "Dockerfile", "CMakeLists.txt"
            // TODO: expand
        )

        private val pageFileNames = setOf(
            "html", "htm"
        )
    }

    val mimeType: String
        get() {
            return mime ?: try {
                mime = Files.probeContentType(file.toPath()) ?: "application/octet-stream"
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

    val isPage: Boolean
        get() {
            return pageFileNames.contains(FilenameUtils.getExtension(name.toLowerCase()))
        }

    val isDisplayableImage: Boolean
        get() {
            return displayableImageTypes.contains(mimeType)
        }

    val browserSafeMimeType: String
        get() {
            val mime = mimeType
            return if (safeMimetype.contains(mime)) mime else "application/octet-stream"
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RepositoryFile) return false

        if (relativePath != other.relativePath) return false

        return true
    }

    override fun hashCode(): Int {
        return relativePath.hashCode()
    }

    override fun needsMetadata(): Boolean { return metadataEmpty() }
    // TODO: check for hidden files

    private fun metadataEmpty(): Boolean {
        return title == null && author == null && description == null && thumbnail == null
    }
}
