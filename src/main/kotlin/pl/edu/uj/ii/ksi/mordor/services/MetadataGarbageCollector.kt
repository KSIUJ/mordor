package pl.edu.uj.ii.ksi.mordor.services

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository

@Service
class MetadataGarbageCollector(
    val metadataRepository: FileMetadataRepository,
    val entryRepository: FileEntryRepository,
    val fileEntryService: FileEntryService
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

    private fun checkMetadata(metadata: FileMetadata?) {
        if (metadata != null) {
            entryRepository.findAllByMetadata(metadata)?.forEach { entry -> fileEntryService.checkFile(entry) }
        }
    }
}
