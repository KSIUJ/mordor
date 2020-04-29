package pl.edu.uj.ii.ksi.mordor.services.upload.session

import java.time.LocalDateTime
import java.util.UUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamSource
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class FileUploadSessionService(
    private val sessionRepository: FileUploadSessionRepository,
    private val repositoryService: RepositoryService,
    @Value("\${mordor.pending_sessions_path:}") private val pendingSessionsPath: String
) {
    fun getAllFiles(uploadSession: FileUploadSession): List<RepositoryEntity> {
        val path = sessionRepository.getPathOfSession(uploadSession)
        val entity = repositoryService.getEntity(path) as RepositoryDirectory
        return entity.getChildren()
    }

    fun approve(uploadSession: FileUploadSession) {
        val currentPath = sessionRepository.getPathOfSession(uploadSession)
        val destinationPath = "."
        repositoryService.move(currentPath, destinationPath)
    }

    fun reject(uploadSession: FileUploadSession) {
        sessionRepository.delete(uploadSession)
    }

    fun saveFileInSession(session: FileUploadSession, path: String, streamSource: InputStreamSource) {
        val filePath = sessionRepository.getPathOfSession(session = session) + path
        repositoryService.saveFile(path, streamSource)
    }

    fun createSingleFileSession(user: User, path: String, streamSource: InputStreamSource): FileUploadSession {
        val sessionId = UUID.randomUUID().toString()
        val session = FileUploadSession(sessionId, user, LocalDateTime.now())
        sessionRepository.save(session)
        saveFileInSession(session, path, streamSource)
        return session
    }
}
