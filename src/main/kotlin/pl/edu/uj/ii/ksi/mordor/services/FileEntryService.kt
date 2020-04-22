package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class FileEntryService(
    private val repositoryService: RepositoryService,
    private val entryRepository: FileEntryRepository
) {
    fun fileExists(entry: FileEntry): Boolean {
        return repositoryService.getEntity(entry.path) != null
    }

    fun checkFile(entry: FileEntry) {
        if (!fileExists(entry)) {
            entryRepository.delete(entry)
        }
    }

    fun isFileValidEntryCandidate(file: File): Boolean {
        return file.exists() && !file.isDirectory && !file.isHidden
    }
}
