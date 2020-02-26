package pl.edu.uj.ii.ksi.mordor.services

import org.apache.tika.Tika
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.sax.BodyContentHandler
import org.springframework.util.DigestUtils
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata

@Service
class TikaExtractor {
    fun extractText(file: File): String {
        return Tika().parse(file).readText()
    }

    fun extractMetadata(file: File): FileMetadata {
        val metadata = getMetadata(file)
        return FileMetadata(
                author = checkNullValue(metadata.get("Author")),
                description = checkNullValue(metadata.get("Subject")),
                fileHash = getHash(file),
                title = checkNullValue(metadata.get("Title")),
                mimeType = Tika().detect(file),
                crawledContent = null,
                thumbnail = null,
                files = emptyList()
        )
    }

    private fun checkNullValue(value: String?): String? {
        if (value == "null") {
            return ""
        }
        return value
    }

     fun getHash(file: File): String {
        return DigestUtils.md5DigestAsHex(file.readText().toByteArray())
    }

    private fun getMetadata(file: File): Metadata {
        val metadata = Metadata()
        AutoDetectParser().parse(FileInputStream(file), BodyContentHandler(), metadata, ParseContext())
        return metadata
    }
}