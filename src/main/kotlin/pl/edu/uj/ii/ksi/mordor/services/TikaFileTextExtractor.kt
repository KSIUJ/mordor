package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.apache.tika.Tika
import org.springframework.stereotype.Service

@Service
class TikaFileTextExtractor(private val tika: Tika) : FileTextExtractor {
    override fun extract(file: File): String? {
        return tika.parse(file).readText()
    }
}
