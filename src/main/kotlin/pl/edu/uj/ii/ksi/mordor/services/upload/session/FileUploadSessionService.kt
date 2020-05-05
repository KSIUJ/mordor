package pl.edu.uj.ii.ksi.mordor.services.upload.session

import java.time.LocalDateTime
import java.util.UUID
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.services.FileEntryCreator
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class FileUploadSessionService(
    private val sessionRepository: FileUploadSessionRepository,
    private val repositoryService: RepositoryService,
    private val entryCreator: FileEntryCreator,
    private val entryRepository: FileEntryRepository
) {
    fun getRepositoryServiceOfSession(uploadSession: FileUploadSession): RepositoryService {
        val path = sessionRepository.getPathOfSession(uploadSession)
        val absolutePath = repositoryService.getAbsolutePath(path).toString()
        return RepositoryService(absolutePath, entryRepository, entryCreator)
    }

    fun approve(uploadSession: FileUploadSession) {
        val currentPath = sessionRepository.getPathOfSession(uploadSession)
        val destinationPath = "."
        repositoryService.move(currentPath, destinationPath)
    }

    fun reject(uploadSession: FileUploadSession) {
        sessionRepository.delete(uploadSession)
    }

    fun createFileSession(user: User): FileUploadSession {
        val sessionId = UUID.randomUUID().toString()
        val session = FileUploadSession(sessionId, user, LocalDateTime.now())
        sessionRepository.save(session)
        return session
    }
}
