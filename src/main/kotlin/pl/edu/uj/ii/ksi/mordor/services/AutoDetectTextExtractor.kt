package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import java.io.IOException
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.TesseractException
import org.apache.tika.Tika
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class AutoDetectTextExtractor(private val tika: Tika,
                              private val tesseract: Tesseract) : FileTextExtractor{

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    override fun extract(file: File, maxLength: Int): String? {
        try {
            val tikaContent = TikaFileTextExtractor(tika).extract(file)
            if (!isScanned(tikaContent))
                return tikaContent
            val type = tika.detect(file)
            if (type == "application/pdf") {
                return PDFTextExtractor(tesseract).extract(file)
            }
            if (type.startsWith("image")) {
                return ImageTextExtractor(tesseract).extract(file)
            }
        } catch (e: TesseractException) {
            logger.error("Tesseract is unable do process OCR", e)
        } catch (e: IOException) {
            logger.error("File can not be read", e)
        }
        return null
    }

    private fun isScanned(content: String?): Boolean {
        if (content == null)
            return true
        return content.trim().isEmpty()
    }

}