package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import javax.transaction.Transactional

interface FileEntryRepository : JpaRepository<FileEntry, String> {
    fun existsByPath(path: String): Boolean?
    fun findAllByMetadata(metadata: FileMetadata): List<FileEntry>?
    @Transactional
    fun deleteByPath(path: String)
}
