package pl.edu.uj.ii.ksi.mordor.services.crawlers

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository
import pl.edu.uj.ii.ksi.mordor.services.ExternalUserService
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
@ConditionalOnProperty(value = ["mordor.crawlers.enabled"], havingValue = "true", matchIfMissing = false)
class MetadataGarbageCollector(
    val metadataRepository: FileMetadataRepository,
    val entryRepository: FileEntryRepository,
    val repositoryService: RepositoryService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ExternalUserService::class.java)
        var progress = CrawlerProgress()
    }

    @Scheduled(fixedDelay = 60 * 1000 * 1000)
    @Suppress("MagicNumber")
    @Synchronized
    fun collect() {
        logger.info("Metadata garbage collection started")
        progress.active = true
        val metadata = metadataRepository.findAll()
        progress.total = metadata.size.toLong()
        metadata.forEach { fileMetadata ->
            checkMetadata(fileMetadata)
            progress.done += 1
            logger.debug("Metadata garbage collection completed in %.2f percent"
                    .format(progress.currentProgress() * 100))
        }
        logger.info("Metadata garbage collection finished successfully")
        progress.active = false
    }

    private fun checkMetadata(metadata: FileMetadata) {
        entryRepository.findAllByMetadata(metadata)?.forEach { entry -> checkFile(entry) }
    }

    private fun checkFile(entry: FileEntry) {
        logger.debug("Checking entry ${entry.path}")
        try {
            if (!repositoryService.fileExists(entry.path)) {
                logger.debug("File doesn't exists, deleting entry ${entry.path}")
                entryRepository.delete(entry)
            }
        } catch (e: BadRequestException) {
            logger.debug("File outside of repository root, deleting entry ${entry.path}")
            entryRepository.delete(entry)
        }
    }
}
