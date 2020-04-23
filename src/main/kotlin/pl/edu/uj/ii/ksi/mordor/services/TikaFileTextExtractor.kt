package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.apache.tika.Tika
import org.springframework.stereotype.Service

@Service
class TikaFileTextExtractor(private val tika: Tika) : FileTextExtractor {
    private var maxLength: Int? = null

    override fun maxLength(maxLength: Int): FileTextExtractor {
        this.maxLength = maxLength
        return this
    }

    override fun extract(file: File): String? {
        val text = tika.parse(file).readText()
        return trim(text)
    }

    private fun trim(text: String): String {
        return if (maxLength != null) {
            text.toCharArray().take(maxLength ?: text.length).toString()
        } else {
            text
        }
    }
}
