package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File
import org.slf4j.LoggerFactory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

abstract class ThumbnailExtractor {

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    val width: Int
        get() = 200

    val height: Int
        get() = 200

    val transparent: Int
        get() = 0x00FFFFFF

    var next: ThumbnailExtractor? = null

    fun addNext(extractor: ThumbnailExtractor?) {
        this.next = extractor
    }

    open fun extract(file: File): ByteArray? {
        return null
    }

    open fun canParse(file: File): Boolean {
        return true
    }

    open fun parse(file: File): ByteArray? {
        if (canParse(file)) {
            logger.info("Extracting thumbnail for " + file.absolutePath + " using " + this.javaClass.name)
            return extract(file)
        } else if (next != null) {
            return next!!.parse(file)
        }
        logger.warn("No thumbnail extractor for file " + file.absolutePath)
        return null
    }
}
