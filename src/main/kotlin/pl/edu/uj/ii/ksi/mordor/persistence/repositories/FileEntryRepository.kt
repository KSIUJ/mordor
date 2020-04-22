package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata

interface FileEntryRepository : JpaRepository<FileEntry, String> {
    fun findAllByMetadata(metadata: FileMetadata): List<FileEntry>?
}
