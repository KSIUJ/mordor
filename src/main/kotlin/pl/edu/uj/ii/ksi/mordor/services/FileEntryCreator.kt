package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import javax.persistence.EntityManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository
import pl.edu.uj.ii.ksi.mordor.services.hash.FileHashProvider
import pl.edu.uj.ii.ksi.mordor.services.text.extractor.FileTextExtractor

@Service
class FileEntryCreator(
        private val metadataExtractor: MetadataExtractor,
        private val entityManager: EntityManager,
        @Qualifier("autoDetectTextExtractor") private val fileTextExtractor: FileTextExtractor,
        private val hashProvider: FileHashProvider,
        private val metadataRepository: FileMetadataRepository
) {

    companion object {
        private const val contentMaxLength: Int = 24 * 1024
    }

    @Transactional
    fun create(file: File): FileEntry? {
        val hash = hashProvider.calculate(file)
        val metadata = metadataRepository.findByFileHash(hash)
        return if (metadata == null) {
            createNewMetadata(file)
        } else addEntryToExistingMetadata(metadata, file)
    }

    private fun createNewMetadata(file: File): FileEntry? {
        val metadata: FileMetadata? = metadataExtractor.extract(file)
        val contentText: String? = fileTextExtractor.extract(file, contentMaxLength)

        if (metadata == null) return null

        return saveMetadata(metadata, contentText, file)
    }

    private fun saveMetadata(metadata: FileMetadata, contentText: String?, file: File): FileEntry? {
        entityManager.persist(metadata)
        val content = FileContent(id = metadata.id, text = contentText, file = metadata)
        metadata.crawledContent = content
        val result = addEntryToExistingMetadata(metadata, file)
        entityManager.persist(content)
//        entityManager.persist(thumbnail)
        return result
    }

    private fun addEntryToExistingMetadata(metadata: FileMetadata, file: File): FileEntry? {
        val entry = FileEntry(path = file.path, metadata = metadata)
        metadata.files?.plus(entry)
        entityManager.persist(metadata)
        entityManager.persist(entry)
        return entry
    }
}
