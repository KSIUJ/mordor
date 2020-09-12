package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File
import org.apache.tika.Tika
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class ThumbnailAutoExtractor(tika: Tika) : ThumbnailExtractor() {

    private val extractors = listOf(ImageThumbnailExtractor(tika), PDFThumbnailExtractor(tika))

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    override fun extract(file: File): ByteArray? {
        for (extractor in extractors) {
            if (extractor.canParse(file)) {
                return extractor.extract(file)
            }
        }
        logger.warn("No thumbnail extractor for file " + file.absolutePath)
        return null
    }
}
