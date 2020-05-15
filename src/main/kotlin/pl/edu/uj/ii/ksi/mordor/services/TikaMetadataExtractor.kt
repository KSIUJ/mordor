package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import java.io.IOException
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.services.hash.FileHashProvider
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class TikaMetadataExtractor(private val tika: Tika, private val hashProvider: FileHashProvider) : MetadataExtractor {
    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    override fun extract(file: File): FileMetadata? {
        return try {
            val mimeType = tika.detect(file)
            val metadata = getTikaMetadata(file)
            val title = metadata.get(TikaCoreProperties.TITLE) ?: file.nameWithoutExtension

            FileMetadata(
                    author = metadata.get(TikaCoreProperties.CREATOR),
                    description = metadata.get(TikaCoreProperties.DESCRIPTION),
                    fileHash = hashProvider.calculate(file),
                    title = title,
                    mimeType = mimeType,
                    thumbnail = null,
                    crawledContent = null
            )
        } catch (e: IOException) {
            logger.warn("Extraction of metadata text failed, returning empty metadata instead", e)
            null
        }
    }

    private fun getTikaMetadata(file: File): Metadata {
        val metadata = Metadata()
        tika.parse(file, metadata)
        return metadata
    }
}
