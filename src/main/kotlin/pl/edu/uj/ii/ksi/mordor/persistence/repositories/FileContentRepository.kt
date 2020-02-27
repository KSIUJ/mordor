package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import javax.transaction.Transactional

interface FileContentRepository : JpaRepository<FileContent, Long> {
    @Transactional
    fun deleteByFile(id: FileMetadata?)
}
