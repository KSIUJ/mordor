package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata

interface MetadataExtractor {
    fun extract(file: File): FileMetadata?
}
