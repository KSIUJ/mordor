package pl.edu.uj.ii.ksi.mordor.services.text.extractor

import org.slf4j.LoggerFactory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

class FileContentValidator {

    private val minAlphanumericsPercent = 0.6

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    fun isValid(content: String?): Boolean {
        return content.isNullOrEmpty() || whiteSpaceFilter(content)
    }

    private fun whiteSpaceFilter(content: String): Boolean {
        val letters = content.filter { c -> c.isLetterOrDigit() }.length
        if (letters.toFloat().div(content.length) < minAlphanumericsPercent) {
            logger.warn("Number of alphanumeric chars is less than 60%. OCR result will be turned to null")
            return false
        }
        return true
    }
}
