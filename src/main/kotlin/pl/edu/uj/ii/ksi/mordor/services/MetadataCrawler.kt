package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class MetadataCrawler(
    @Value("\${mordor.root_path}") private val rootPathStr: String,
    val repositoryService: RepositoryService,
    val fileEntryRepository: FileEntryRepository,
    val fileEntryCreator: FileEntryCreator
) {

    companion object {
        private val logger = LoggerFactory.getLogger(ExternalUserService::class.java)
    }

    @Scheduled(fixedDelay = 60 * 1000 * 1000)
    fun crawl() {
        logger.info("Metadata gathering started")
        File(rootPathStr).walkTopDown()
                .filter { file -> needMetadata(file) }
                .forEach { file ->
                    fileEntryCreator.create(file) }
        logger.info("Metadata gathering finished successfully")
    }

    private fun needMetadata(file: File): Boolean {
        val fileNeedsMetadata = repositoryService.getEntity(file.path)?.needsMetadata() ?: false
        return fileNeedsMetadata && !fileEntryRepository.existsById(file.path)
    }
}
