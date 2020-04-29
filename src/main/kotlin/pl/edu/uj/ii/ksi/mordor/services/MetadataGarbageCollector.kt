package pl.edu.uj.ii.ksi.mordor.services

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class MetadataGarbageCollector(
    val metadataRepository: FileMetadataRepository,
    val entryRepository: FileEntryRepository,
    val repositoryService: RepositoryService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ExternalUserService::class.java)
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    fun collect() {
        logger.info("Metadata garbage collection started")
        // TODO count done/total
        metadataRepository.findAll().forEach { metadata -> checkMetadata(metadata) }
        logger.info("Metadata garbage collection finished successfully")
    }

    private fun checkMetadata(metadata: FileMetadata) {
        entryRepository.findAllByMetadata(metadata)?.forEach { entry -> checkFile(entry) }
    }

    private fun checkFile(entry: FileEntry) {
        if (!repositoryService.fileExists(entry.path)) {
            entryRepository.delete(entry)
        }
    }
}