package pl.edu.uj.ii.ksi.mordor.services.text.extractor

import org.slf4j.LoggerFactory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

class FileContentValidator {

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    fun isValid(content: String?): Boolean {
        if (content.isNullOrEmpty()) {
            return true
        }
        return whiteSpaceFilter(content)
    }

    private fun whiteSpaceFilter(content: String): Boolean {
        val letters = content.filter { c -> c.isLetterOrDigit() }.length
        if (letters.toFloat().div(content.length) < 0.25) {
            logger.warn("Number of white chars is more than 75%. OCR result will be turned to null")
            return false
        }
        return true
    }
}