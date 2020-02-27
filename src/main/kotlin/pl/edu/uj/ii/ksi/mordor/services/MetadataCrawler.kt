package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileThumbnail
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileContentRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileThumbnailRepository
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

@Service
class MetadataCrawler(@Value("\${mordor.root_path}") private val root: String, @Value("\${spring.mvc.static-path-pattern}") private val pattern: String, private val entryRepository: FileEntryRepository, private val metadataRepository: FileMetadataRepository, private val contentRepository: FileContentRepository, private val thumbnailRepository: FileThumbnailRepository) {

    @Scheduled(fixedDelay = 5 * 1000)
    fun crawl() {
        System.out.println("gathering")
        val path = root + pattern
        gather(path.substring(0,path.length - 2))
    }
    fun gather(pathname: String) {
        File(pathname).walkTopDown()
                .filter { file -> file.isFile() && !entryRepository.existsByPath(file.absolutePath)!! }
                .forEach { file -> gatherMetadata(file) }
    }

    private fun gatherMetadata(file: File) {
        val metadata = TikaExtractor.extractMetadata(file)

        System.out.printf("Adding %s\n", file.absolutePath)

        metadata.fileHash?.let {
            val hash = metadata.fileHash!!
            if (metadataRepository.existsByFileHash(hash)!!) {
                saveNewEntryForExistingMetadata(hash, file)
                return
            }
        }
        saveNewMetadata(metadata, file)
    }

    private fun saveNewMetadata(metadata: FileMetadata, file: File) {
        metadataRepository.save(metadata)
        val thumbnail = FileThumbnail(file = metadata, thumbnail = ByteArray(size = 0))
        thumbnailRepository.save(thumbnail)
        val entry = FileEntry(path = file.absolutePath, metadata = metadata)
        val content = FileContent(file = metadata, text = TikaExtractor.extractText(file))
        entryRepository.save(entry)
        contentRepository.save(content)
    }

    private fun saveNewEntryForExistingMetadata(hash: String, file: File) {
        val metadata = metadataRepository.findByFileHash(hash)!!
        val entry = FileEntry(path = file.absolutePath, metadata = metadata)
        entryRepository.save(entry)
    }
}