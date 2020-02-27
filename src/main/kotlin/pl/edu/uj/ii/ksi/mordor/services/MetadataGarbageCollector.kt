package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileContentRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileThumbnailRepository
import java.nio.file.Files
import java.nio.file.Path

@Service
class MetadataGarbageCollector(private val entryRepository: FileEntryRepository, private val metadataRepository: FileMetadataRepository, private val fileContentRepository: FileContentRepository, private val thumbnailRepository: FileThumbnailRepository) {
    @Scheduled(fixedDelay = 5 * 1000)
    fun collect() {
        System.out.println("removing")

        metadataRepository.findAll().forEach{metadata -> collectMetadata(metadata)}
    }

    private fun collectMetadata(metadata: FileMetadata?) {
        if (metadata != null) {
            entryRepository.findAllByMetadata(metadata)?.forEach({ entry -> collectEntry(entry!!)})
            if (listOf(entryRepository.findAllByMetadata(metadata))[0]?.isEmpty()!!) {
                removeMetadata(metadata)
            }
        }
    }

    private fun removeMetadata(metadata: FileMetadata?) {
        fileContentRepository.deleteByFile(metadata)
        thumbnailRepository.deleteByFile(metadata)
        metadataRepository.deleteById(metadata?.id!!)
    }

    private fun collectEntry(entry: FileEntry) {
        if(!Files.exists(Path.of(entry.path))) {
            System.out.printf("removing %s\n", entry.path)
           entryRepository.deleteByPath(entry.path)
        }
    }
}