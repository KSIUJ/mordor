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
        val metadata = findOrCreateMetadata(file)
        checkOrCreateContent(file, metadata)
        return createEntry(file, metadata)
    }

    private fun checkOrCreateContent(file: File, metadata: FileMetadata) {
        val id = metadata.crawledContent?.id
        if (id ?.let { contentRepository.existsById(it) } == true) {
            createContent(file, metadata)
        }
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

    private fun findOrCreateMetadata(file: File): FileMetadata {
        // TODO: hash is calculated twice
        val hash = metadataExtractor.calculateHash(file)
        val sameHashMetadata = metadataRepository.findByFileHash(hash)
        return checkTheSameHashMetadata(sameHashMetadata, file)
    }

    private fun checkTheSameHashMetadata(sameHashMetadata: FileMetadata?, file: File): FileMetadata {
        if (sameHashMetadata != null) {
            return sameHashMetadata
        }
        return saveMetadata(file)
    }

    private fun saveMetadata(file: File): FileMetadata {
        val metadata = metadataExtractor.extract(file = file)
        return metadataRepository.save(metadata)
    }
}
