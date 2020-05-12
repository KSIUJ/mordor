package pl.edu.uj.ii.ksi.mordor.services.text.extractor

import java.io.File

interface FileTextExtractor {
    fun extract(file: File, maxLength: Int = -1): String?
}
