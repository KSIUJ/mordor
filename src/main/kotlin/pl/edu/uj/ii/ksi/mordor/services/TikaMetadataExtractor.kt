package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.services.hash.FileHashProvider

@Service
class TikaMetadataExtractor(private val tika: Tika, private val hashProvider: FileHashProvider) : MetadataExtractor {
    override fun extract(file: File): FileMetadata {
        val tikaMetadata = getTikaMetadata(file)
        return FileMetadata(
                author = tikaMetadata.get("Author"),
                description = tikaMetadata.get("Subject"),
                fileHash = hashProvider.calculate(file),
                title = tikaMetadata.get("Author"),
                mimeType = tikaMetadata.get("Author"),
                thumbnail = null,
                crawledContent = null
        )
    }

    private fun getTikaMetadata(file: File): Metadata {
        val metadata = Metadata()
        tika.parse(file, metadata)
        return metadata
    }
}
