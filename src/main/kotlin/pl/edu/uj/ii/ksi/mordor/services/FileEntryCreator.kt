package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.services.hash.FileHashProvider
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
@Transactional
class FileEntryCreator(
    private val metadataExtractor: MetadataExtractor,
    private val entityManager: EntityManager,
    private val fileTextExtractor: FileTextExtractor,
    private val hashProvider: FileHashProvider
) {

    companion object {
        // TODO: move to config
        private const val contentMaxLength: Int = 24 * 1024
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    fun create(file: File): FileEntry? {
        val metadata = findMetadataWithSameHash(file)
        return if (metadata == null) {
            createNewMetadata(file)
        } else addEntryToExistingMetadata(metadata, file)
    }

    private fun createNewMetadata(file: File): FileEntry? {
        val metadata: FileMetadata? = metadataExtractor.extract(file)
        val contentText: String? = fileTextExtractor.extract(file, contentMaxLength)

        if (metadata == null || contentText == null) return null

        return saveMetadata(metadata, contentText, file)
    }

    private fun saveMetadata(metadata: FileMetadata, contentText: String, file: File): FileEntry? {
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

    private fun findMetadataWithSameHash(file: File): FileMetadata? {
        val hash = hashProvider.calculate(file)
        val builder = entityManager.criteriaBuilder
        var query: CriteriaQuery<FileMetadata> = builder.createQuery(FileMetadata::class.java)
        val criteria: Root<FileMetadata> = query.from(FileMetadata::class.java)
        query = query
                .select(criteria)
                .where(builder.equal(criteria.get<String>("fileHash"), hash))

        return entityManager.createQuery(query).resultList.getOrNull(0)
    }
}
