package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class MetadataCrawler(
    @Value("\${spring.mvc.static-path-pattern}") private val pattern: String,
    @Value("\${mordor.root_path}") private val rootPathStr: String,
    val repositoryService: RepositoryService,
    val fileEntryCreator: FileEntryCreator
) {

    companion object {
        private val logger = LoggerFactory.getLogger(ExternalUserService::class.java)
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    fun crawl() {
        logger.info("Metadata gathering started")
        File(rootPathStr).walkTopDown()
                .filter { file -> needMetadata(file) }
                .forEach { file -> fileEntryCreator.create(file) }
        logger.info("Metadata gathering finished successfully")
    }

    private fun needMetadata(file: File): Boolean {
        return repositoryService.getEntity(file.path)?.needsMetadata() ?: false
    }
}
