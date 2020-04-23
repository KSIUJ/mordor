package pl.edu.uj.ii.ksi.mordor.services

import java.io.File

interface FileTextExtractor {
    fun extract(file: File): String?
    fun maxLength(maxLength: Int): FileTextExtractor
}
