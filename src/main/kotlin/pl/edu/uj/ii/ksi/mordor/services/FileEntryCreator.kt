package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileContentRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository

@Service
class FileEntryCreator(
    private val metadataExtractor: MetadataExtractor,
    private val entryRepository: FileEntryRepository,
    private val metadataRepository: FileMetadataRepository,
    private val contentRepository: FileContentRepository,
    private val fileTextExtractor: FileTextExtractor
) {

    fun create(file: File): FileEntry {
        val metadata = getMetadata(file)
        createContent(file, metadata)
        return createEntry(file, metadata)
    }

    private fun createContent(file: File, metadata: FileMetadata) {
        val text = fileTextExtractor.extract(file)!!
        val content = FileContent(text = text, file = metadata)
        contentRepository.save(content)
    }

    private fun createEntry(file: File, metadata: FileMetadata): FileEntry {
        val entry = FileEntry(path = file.path, metadata = metadata)
        return entryRepository.save(entry)
    }

    private fun getMetadata(file: File): FileMetadata {
        val metadata = metadataExtractor.extract(file)
        return checkIfFileHashIsUnique(metadata)
    }

    private fun checkIfFileHashIsUnique(metadata: FileMetadata): FileMetadata {
        val sameHashMetadata = metadata.fileHash?.let { metadataRepository.findByFileHash(it) }
        if (sameHashMetadata != null) {
            return sameHashMetadata
        }
        return saveMetadata(metadata)
    }

    private fun saveMetadata(metadata: FileMetadata): FileMetadata {
        return metadataRepository.save(metadata)
    }
}
