package pl.edu.uj.ii.ksi.mordor.services.upload.session

import java.lang.IllegalArgumentException
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Optional
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Suppress("NotImplementedDeclaration", "TooManyFunctions")
@Service
class FileUploadSessionRepository(
    private val repositoryService: RepositoryService,
    private val userRepository: UserRepository,
    @Value("\${mordor.pending_sessions_path:}") private val pendingSessionsPath: String
) : PagingAndSortingRepository<FileUploadSession, Pair<Long, String>> {

    override fun <S : FileUploadSession> save(session: S): S {
        val absolutePath = repositoryService.getAbsolutePath(getPathOfSession(session))
        absolutePath.toFile().mkdirs()
        return session
    }

    override fun findAll(sort: Sort): MutableIterable<FileUploadSession> {
        TODO("Not yet implemented")
    }

    override fun findAll(pageable: Pageable): Page<FileUploadSession> {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<FileUploadSession> {
        return findAllById(ids = findAllIds().toMutableList())
    }

    override fun deleteById(id: Pair<Long, String>) {
        repositoryService.delete(getPathOfId(id))
    }

    override fun deleteAll(sessions: MutableIterable<FileUploadSession>) {
        sessions.forEach { session -> delete(session) }
    }

    override fun deleteAll() {
        findAll().forEach { session -> delete(session) }
    }

    override fun <S : FileUploadSession?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        TODO("Not yet implemented")
    }

    override fun count(): Long {
        return findAllIds().size.toLong()
    }

    override fun findAllById(ids: MutableIterable<Pair<Long, String>>): List<FileUploadSession> {
        return ids.map { id -> findById(id) }
                .filter { optional -> optional.isPresent }
                .map { optional -> optional.get() }
    }

    override fun existsById(id: Pair<Long, String>): Boolean {
        return findById(id).isPresent
    }

    override fun findById(id: Pair<Long, String>): Optional<FileUploadSession> {
        val path = getPathOfId(id)
        return if (repositoryService.fileExists(path)) {

            val timestamp = repositoryService.getAbsolutePath(path).toFile().lastModified()
            val datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())

            Optional.of(FileUploadSession(
                    user = userRepository.findById(id.first).get(),
                    id = id.second,
                    creationDate = datetime))
        } else {
            Optional.empty()
        }
    }

    override fun delete(session: FileUploadSession) {
        val id = Pair(session.user.id!!, session.id)
        deleteById(id)
    }

    private fun getIdOfPath(path: Path): Pair<Long, String> {
        try {
            val sessionId = path.fileName.toString()
            val userId = path.parent.fileName.toString().toLong()
            return Pair(userId, sessionId)
        } catch (e: NumberFormatException) {
            // TODO better exception handling
            throw IllegalArgumentException("Path $path is not uploadSession folder's path")
        }
    }

    private fun findAllIds(): List<Pair<Long, String>> {
        return when (val sessions = repositoryService.getEntity(pendingSessionsPath)) {
            null -> emptyList()
            else -> {
                val sessionsDirectory = sessions as RepositoryDirectory
                sessionsDirectory.getChildren()
                        .filterIsInstance<RepositoryDirectory>()
                        .flatMap { userFolder ->
                            userFolder.getChildren()
                                    .filterIsInstance<RepositoryDirectory>()
                                    .map { getIdOfPath(Paths.get(it.relativePath)) }
                        }
            }
        }
    }

    fun getPathOfId(id: Pair<Long, String>): String {
        return "$pendingSessionsPath/${id.first}/${id.second}"
    }

    fun getPathOfSession(session: FileUploadSession): String {
        return getPathOfId(Pair(session.user.id!!, session.id))
    }
}
