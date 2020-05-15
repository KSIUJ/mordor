package pl.edu.uj.ii.ksi.mordor.services.repository

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.services.FileEntryCreator

@Service
class RepositoryService(
    @Value("\${mordor.root_path}") private val rootPathStr: String,
    private val entryRepository: FileEntryRepository,
    private val entryCreator: FileEntryCreator
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    private final val rootPath: Path = Paths.get(rootPathStr)

    /**
     * Resolves path to an absolute one beginning in the repository root
     *
     * @param path path to be resolved
     * @return absolute path beginning in repository root
     * @throws BadRequestException when path is reaching for a file outside of repository root
     */
    fun getAbsolutePath(path: String): Path {
        val fullPath = rootPath.resolve(path).normalize().toAbsolutePath()
        if (!fullPath.startsWith(rootPath)) {
            logger.debug("Tried to access file outside of repository root: $path")
            throw BadRequestException("invalid path")
        }
        return fullPath
    }

    fun getEntity(entityPath: String): RepositoryEntity? {
        val path = getAbsolutePath(entityPath)
        return getRepositoryEntity(path)
    }

    fun fileExists(path: String): Boolean {
        return getAbsolutePath(path).toFile().exists()
    }

    fun saveFile(path: String, inputStream: InputStream): RepositoryEntity? {
        val outputPath = getAbsolutePath(path)

        outputPath.parent.toFile().mkdirs()
        val outputStream = FileOutputStream(outputPath.toString())
        inputStream.copyTo(outputStream)
        entryCreator.create(outputPath.toFile())

        return getEntity(outputPath.toString())
    }

    @Transactional
    fun move(from: String, to: String, recursive: Boolean = false) {
        val absoluteToPath = getAbsolutePath(to)
        val entity = getEntity(from)
        val absoluteFromPath = getAbsolutePath(from)

        if (entity is RepositoryFile) {
            Files.move(absoluteFromPath, absoluteToPath, StandardCopyOption.REPLACE_EXISTING)
            moveEntry(absoluteToPath, absoluteFromPath)
        } else if (entity is RepositoryDirectory && recursive) {
            absoluteToPath.toFile().mkdir()
            entity.getChildren().forEach { child ->
                val toEntityPath = "$to/${Paths.get(child.relativePath).fileName}"
                val fromEntityPath = "$from/${Paths.get(child.relativePath).fileName}"
                move(fromEntityPath, toEntityPath, recursive)
            }
            Files.delete(absoluteFromPath)
        }
    }

    private fun moveEntry(absoluteToPath: Path, absoluteFromPath: Path) {
        if (entryRepository.existsById(absoluteToPath.toString())) {
            entryRepository.deleteById(absoluteToPath.toString())
        }
        val entryResult: Optional<FileEntry> = entryRepository.findById(absoluteFromPath.toString())

        entryCreator.create(absoluteToPath.toFile())
        if (entryResult.isPresent) {
            entryRepository.delete(entryResult.get())
        }
    }

    @Transactional
    fun delete(path: String, recursive: Boolean = false) {
        val absolutePath = getAbsolutePath(path)
        val entity = getEntity(path)

        if (entity is RepositoryFile) {
            Files.delete(absolutePath)
            entryRepository.deleteById(absolutePath.toString())
        } else if (entity is RepositoryDirectory && recursive) {
            entity.getChildren().forEach { child ->
                val entityPath = entity.relativePath + "/" + Paths.get(child.relativePath).fileName
                delete(entityPath, recursive)
            }
            Files.delete(absolutePath)
        }
    }

    private fun getDirectoryChildren(fullPath: Path, includeHiddenFiles: Boolean): List<RepositoryEntity> {
        val stream = Files.newDirectoryStream(fullPath)
        val children = mutableListOf<RepositoryEntity>()

        stream.use {
            stream.forEach { f ->
                if (!f.toFile().isHidden || includeHiddenFiles) {
                    getRepositoryEntity(f)?.let { p -> children.add(p) }
                }
            }
        }

        return children
    }

    private fun getRepositoryEntity(fullPath: Path): RepositoryEntity? {
        val file: File = fullPath.toFile()
        when {
            !file.exists() -> return null
            !file.canRead() -> return null
            file.isDirectory -> return object : RepositoryDirectory(file.name,
                    rootPath.relativize(fullPath).toString()) {
                override fun getChildren(includeHiddenFiles: Boolean): List<RepositoryEntity> {
                    return getDirectoryChildren(fullPath, includeHiddenFiles)
                }
            }
            else -> return returnRepositoryFile(file, fullPath)
        }
    }

    private fun returnRepositoryFile(file: File, fullPath: Path): RepositoryEntity? {
        val entry: Optional<FileEntry> = entryRepository.findById(file.path)
        return if (entry.isPresent) {
            val metadata = entry.get().metadata!!
            val thumbnail = if (metadata.thumbnail?.thumbnail == null) null else "/thumbnail" + entry.get().path

            RepositoryFile(file.name, rootPath.relativize(fullPath).toString(), file,
                    metadata.title, metadata.author, metadata.description, metadata.mimeType, thumbnail)
        } else {
            RepositoryFile(file.name, rootPath.relativize(fullPath).toString(), file,
                    null, null, null, null, null)
        }
    }
}
