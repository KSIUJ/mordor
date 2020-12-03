package pl.edu.uj.ii.ksi.mordor.services.upload.session

import java.time.LocalDateTime
import java.util.UUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.services.FileEntryCreator
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class UploadSessionService(
    @Value("\${mordor.allow_metadata_gathering}") private val metadataGatheringAllowed: Boolean,
    private val sessionRepository: UploadSessionRepository,
    private val repositoryService: RepositoryService,
    private val entryCreator: FileEntryCreator,
    private val entryRepository: FileEntryRepository
) {
    fun getRepositoryService(session: UploadSession): RepositoryService {
        val path = sessionRepository.getPathOfSession(session)
        val absolutePath = repositoryService.getAbsolutePath(path).toString()
        return RepositoryService(absolutePath, metadataGatheringAllowed, entryRepository, entryCreator)
    }

    fun approve(session: UploadSession) {
        val currentPath = sessionRepository.getPathOfSession(session)
        val destinationPath = "."
        repositoryService.move(currentPath, destinationPath, true)
    }

    fun reject(session: UploadSession) {
        sessionRepository.delete(session)
    }

    fun createFileSession(user: User): UploadSession {
        val sessionId = UUID.randomUUID().toString()
        val session = UploadSession(sessionId, user, LocalDateTime.now())
        sessionRepository.save(session)
        return session
    }
}
