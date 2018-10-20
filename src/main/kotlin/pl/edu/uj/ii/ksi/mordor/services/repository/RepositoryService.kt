package pl.edu.uj.ii.ksi.mordor.services.repository

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

@Service
class RepositoryService(@Value("\${mordor.root_path}") private val rootPathStr: String) {
    private final val rootPath: Path = Paths.get(rootPathStr)

    private fun getRequestedPath(path: String): Path {
        val parts = path.split("/")
        var current = rootPath
        for (i in 2..(parts.size - 1)) {
            val part = parts[i]
            when {
                part == "." || part == ".." -> throw RuntimeException("invalid path")
                part != "" -> current = current.resolve(part)
            }

        }
        return current
    }

    private fun getRepositoryEntity(fullPath: Path): RepositoryEntity? {
        val file: File = fullPath.toFile()

        when {
            !file.exists() -> return null
            !file.canRead() -> return null
            file.isDirectory -> return object : RepositoryDirectory(file.name, rootPath.relativize(fullPath).toString()) {
                override fun getChildren(): List<RepositoryEntity> {
                    val stream = Files.newDirectoryStream(fullPath)
                    val children = mutableListOf<RepositoryEntity>()

                    stream.use {
                        stream.forEach { f ->
                            getRepositoryEntity(f)?.let { p -> children.add(p) }
                        }
                    }

                    return children
                }
            }
            // TODO: add metadata.
            else -> return RepositoryFile(file.name, rootPath.relativize(fullPath).toString(), file,
                    null, null, null, null, null)
        }

    }

    fun getEntity(entityPath: String): RepositoryEntity? {
        val path = getRequestedPath(entityPath)

        return getRepositoryEntity(path)
    }
}
