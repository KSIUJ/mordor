package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileContentRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository
import pl.edu.uj.ii.ksi.mordor.services.hash.FileHashProvider

@Service
class FileEntryCreator(
    private val metadataExtractor: MetadataExtractor,
    private val entryRepository: FileEntryRepository,
    private val metadataRepository: FileMetadataRepository,
    private val contentRepository: FileContentRepository,
    private val fileTextExtractor: FileTextExtractor,
    private val hashProvider: FileHashProvider
) {

    companion object {
        // TODO: move to config
        private const val contentMaxLength: Int = 24 * 1024
    }

    fun create(file: File) {
        val metadata = findMetadataWithSameHash(file)
        if (metadata == null) {
            createAllMetadata(file)
        } else createThenSaveEntry(metadata, file)
    }

    private fun createAllMetadata(file: File) {
        val extracted = extractThenSaveMetadata(file)
        addThenSaveForeignKeys(extracted, file)
    }

    private fun extractThenSaveMetadata(file: File): FileMetadata {
        val extracted = metadataExtractor.extract(file)
        return metadataRepository.save(extracted)
    }

    private fun addThenSaveForeignKeys(metadata: FileMetadata, file: File) {
        val content = createContent(file = file, metadata = metadata)
        metadata.crawledContent = content
        // TODO: val thumbnail = createThumbnail(file, metadata)
        createThenSaveEntry(metadata, file)
        saveContentAndThumbnail(content)
    }

    private fun saveContentAndThumbnail(content: FileContent) {
        contentRepository.save(content)
        // TODO: thumbnailRepository.save(thumbnail)
    }

    private fun createContent(file: File, metadata: FileMetadata): FileContent {
        val extractor = fileTextExtractor.maxLength(contentMaxLength)
        return FileContent(id = metadata.id, text = extractor.extract(file), file = metadata)
    }

    private fun createThenSaveEntry(metadata: FileMetadata, file: File) {
        val entry = FileEntry(path = file.path, metadata = metadata)
        addAndSaveEntryToMetadata(metadata, entry)
        entryRepository.save(entry)
    }

    private fun addAndSaveEntryToMetadata(metadata: FileMetadata, entry: FileEntry) {
        metadata.files?.plus(entry)
        metadataRepository.save(metadata)
    }

    private fun findMetadataWithSameHash(file: File): FileMetadata? {
        val hash = hashProvider.calculate(file)
        return metadataRepository.findByFileHash(hash)
    }
}
