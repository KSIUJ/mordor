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
                return tikaContent
            }
            val type = tika.detect(file)
            if (type == "application/pdf") {
                return PDFTextExtractor(tessBaseAPI).extract(file, maxLength)
            }
            if (type.startsWith("image")) {
                return ImageTextExtractor(tessBaseAPI).extract(file, maxLength)
            }
        } catch (e: IOException) {
            logger.error("File can not be read: " + file.absolutePath, e)
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
        val result = StringBuilder()
        for (seq in content.split("\\s".toRegex())) {
            if (seq.isNotEmpty() && seq.length > minWordLength) {
                result.append("$seq ")
            }
        }
        return result.toString()
    }
}
