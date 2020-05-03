package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import java.io.IOException
import kotlin.math.min
import org.apache.tika.Tika
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class TikaFileTextExtractor(private val tika: Tika) : FileTextExtractor {
    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    override fun extract(file: File, maxLength: Int): String? {
        return try {
            val text = tika.parse(file).readText()
            if (maxLength >= 0) {
                val endIndex = min(text.length, maxLength)
                text.subSequence(0, endIndex).toString()
            } else {
                text
            }
        } catch (e: IOException) {
            logger.warn("Extraction of file text failed, returning null instead", e)
            null
        }
    }
}
