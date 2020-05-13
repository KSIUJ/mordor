package pl.edu.uj.ii.ksi.mordor.services.crawlers

import java.io.File
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.services.ExternalUserService
import pl.edu.uj.ii.ksi.mordor.services.FileEntryCreator
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class MetadataCrawler(
    @Value("\${mordor.root_path}") private val rootPathStr: String,
    private val repositoryService: RepositoryService,
    private val fileEntryRepository: FileEntryRepository,
    private val fileEntryCreator: FileEntryCreator

) {
    companion object {
        private val logger = LoggerFactory.getLogger(ExternalUserService::class.java)
        var progress = CrawlerProgress()
    }

    @Scheduled(fixedDelay = 60 * 1000 * 1000)
    @Suppress("MagicNumber")
    fun crawl() {
        progress.active(true)
        logger.info("Metadata gathering started")
        val files = File(rootPathStr).walkTopDown().filter { file -> needMetadata(file) }.toList()
        logger.debug("Found ${progress.total} files with no metadata")
        progress.total = files.size.toLong()
        files.forEach { file ->
            logger.debug("Creating entry for file ${file.path}")
            logger.debug("Metadata gathering completed in %.2f percent"
                    .format(progress.currentProgress() * 100))
            fileEntryCreator.create(file)
            progress.done += 1
        }
        logger.info("Metadata gathering finished successfully")
        progress.active(false)
    }

    private fun needMetadata(file: File): Boolean {
        val fileNeedsMetadata = repositoryService.getEntity(file.path)?.needsMetadata() ?: false
        return fileNeedsMetadata && !fileEntryRepository.existsById(file.path)
    }
}
