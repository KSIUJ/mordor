package pl.edu.uj.ii.ksi.mordor.services.repository

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException

@Service
class RepositoryService(@Value("\${mordor.root_path}") private val rootPathStr: String) {
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

    private fun getDirectoryChuldren(fullPath: Path, includeHiddenFiles: Boolean): List<RepositoryEntity> {
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
                    return getDirectoryChuldren(fullPath, includeHiddenFiles)
                }
            }
            // TODO: add metadata.
            else -> return RepositoryFile(file.name, rootPath.relativize(fullPath).toString(), file,
                null, null, null, null, null)
        }
    }

    fun getEntity(entityPath: String): RepositoryEntity? {
        val path = getAbsolutePath(entityPath)
        return getRepositoryEntity(path)
    }
}
