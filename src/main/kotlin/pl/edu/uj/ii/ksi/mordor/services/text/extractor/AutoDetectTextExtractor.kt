package pl.edu.uj.ii.ksi.mordor.services.text.extractor

import java.io.File
import java.io.IOException
import java.lang.StringBuilder
import org.apache.tika.Tika
import org.bytedeco.tesseract.TessBaseAPI
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class AutoDetectTextExtractor(
    private val tika: Tika,
    private val tessBaseAPI: TessBaseAPI
) : FileTextExtractor {

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    private val minWordLength = 4

    override fun extract(file: File, maxLength: Int): String? {
        val content = cleanSmallWords(extractRaw(file, maxLength))
        return if (FileContentValidator().isValid(content)) content else null
    }

    private fun extractRaw(file: File, maxLength: Int): String? {
        try {
            val tikaContent = TikaFileTextExtractor(tika).extract(file, maxLength)
            if (!isScanned(tikaContent)) {
                logger.info("Extracted text from " + file.absolutePath + " using Tika")
                return tikaContent
            }
            val type = tika.detect(file)
            if (type == "application/pdf") {
                logger.info("Extracted text from " + file.absolutePath + " using Bytedeco for PDF")
                return PDFTextExtractor(tessBaseAPI).extract(file, maxLength)
            }
            if (type.startsWith("image")) {
                logger.info("Extracted text from " + file.absolutePath + " using Bytedeco")
                return ImageTextExtractor(tessBaseAPI).extract(file, maxLength)
            }
        } catch (e: IOException) {
            logger.error("File can not be read", e)
        }
        return null
    }

    private fun isScanned(content: String?): Boolean {
        return content?.trim()?.isEmpty() ?: true
    }

    private fun cleanSmallWords(content: String?): String? {
        if (content == null) {
            return null
        }
        val modified = content.replace('\n', ' ').replace('\t', ' ')
        val b = StringBuilder()
        for (seq in modified.split(" ")) {
            if (seq.isNotEmpty() && seq.length > minWordLength) {
                b.append("$seq ")
            }
        }
        return b.toString()
    }
}
