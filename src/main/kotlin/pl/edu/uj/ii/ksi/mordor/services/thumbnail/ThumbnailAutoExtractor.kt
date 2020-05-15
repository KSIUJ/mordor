package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File
import org.apache.tika.Tika
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class ThumbnailAutoExtractor : ThumbnailExtractor() {

    companion object {
        private val tika = Tika()
        private val extractors = listOf(ImageThumbnailExtractor(tika), PDFThumbnailExtractor(tika))
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    override fun extract(file: File): ByteArray? {
        for (extractor in extractors) {
            if (extractor.canParse(file)) {
                logger.info("Extracting thumbnail for " + file.absolutePath + " using " + extractor.javaClass.name)
                return extractor.extract(file)
            }
        }
        logger.warn("No thumbnail extractor for file " + file.absolutePath)
        return null
    }
}
