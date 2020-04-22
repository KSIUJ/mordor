package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import java.nio.file.Files
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata

@Service
class TikaMetadataExtractor(private val tika: Tika) : MetadataExtractor {
    override fun extract(file: File): FileMetadata {
        val tikaMetadata = getTikaMetadata(file)
        return FileMetadata(
                author = tikaMetadata.get("Author"),
                description = tikaMetadata.get("Subject"),
                fileHash = calculateHash(file),
                title = tikaMetadata.get("Author"),
                mimeType = tikaMetadata.get("Author"),
                files = ArrayList(),
                thumbnail = null,
                crawledContent = null
        )
    }

    private fun calculateHash(file: File): String? {
        return DigestUtils.md5DigestAsHex(Files.readAllBytes(file.toPath()))
    }

    private fun getTikaMetadata(file: File): Metadata {
        val metadata = Metadata()
        tika.parse(file, metadata)
        return metadata
    }
}
