package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class FileEntryService(
    private var fileUploadSessionService: FileUploadSessionService,
    private val repositoryService: RepositoryService,
    @Value("\${mordor.pending_sessions_path}") private val pendingSessionPath: String,
    private val entryRepository: FileEntryRepository
) {
    fun fileExists(entry: FileEntry): Boolean {
        val fullPath = getFullPath(entry)
        return repositoryService.getEntity(fullPath) != null
    }

    fun checkFile(entry: FileEntry) {
        if (!fileExists(entry)) {
            entryRepository.delete(entry)
        }
    }

    private fun getFullPath(entry: FileEntry): String {
        return getPrefix(entry) + entry.path
    }

    private fun getPrefix(entry: FileEntry): String {
        if (!fileUploadSessionService.isAccepted(entry.session)) {
            return pendingSessionPath + "/" + entry.session.id
        }
        return ""
    }
}
