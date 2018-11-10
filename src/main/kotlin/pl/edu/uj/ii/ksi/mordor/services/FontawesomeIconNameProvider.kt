package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryFile

@Service
class FontawesomeIconNameProvider : IconNameProvider {
    companion object {
        val mimes = mapOf(
            "application/pdf" to "fa-file-pdf",
            "application/vnd.oasis.opendocument.text" to "fa-file-word",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" to "fa-file-word",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template" to "fa-file-word",
            "application/msword" to "fa-file-word",
            "application/rtf" to "fa-file-word",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" to "fa-file-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.slide" to "fa-file-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.slideshow" to "fa-file-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.template" to "fa-file-powerpoint",
            "application/vnd.ms-powerpoint" to "fa-file-powerpoint",
            "application/vnd.oasis.opendocument.presentation" to "fa-file-powerpoint",
            "application/vnd.oasis.opendocument.presentation-template" to "fa-file-powerpoint",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" to "fa-file-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template" to "fa-file-excel",
            "application/vnd.ms-excel" to "fa-file-excel",
            "application/vnd.oasis.opendocument.spreadsheet" to "fa-file-excel",
            "application/vnd.oasis.opendocument.spreadsheet-template" to "fa-file-excel",
            "text/csv" to "fa-file-csv",
            "application/zip" to "fa-file-archive",
            "x-7z-compressed" to "fa-file-archive",
            "application/x-bzip" to "fa-file-archive",
            "application/x-bzip2" to "fa-file-archive",
            "application/x-cpio" to "fa-file-archive",
            "application/x-tar" to "fa-file-archive",
            "application/zip" to "fa-file-archive",
            "application/x-rar-compressed" to "fa-file-archive",
            "application/gzip" to "fa-file-archive"
        )
    }

    override fun getIconName(entity: RepositoryEntity): String {
        if (entity is RepositoryDirectory) {
            return "fa-folder"
        } else if (entity is RepositoryFile) {
            val mime = entity.mimeType
            if (entity.isCode) {
                return "fa-file-code"
            }
            mimes[mime]?.let { return it }
            return when (mime.substringBefore("/")) {
                "image" -> "fa-file-image"
                "video" -> "fa-file-video"
                "audio" -> "fa-file-audio"
                "text" -> "fa-file-alt"
                else -> "fa-file"
            }
        }
        return "fa-file"
    }
}
