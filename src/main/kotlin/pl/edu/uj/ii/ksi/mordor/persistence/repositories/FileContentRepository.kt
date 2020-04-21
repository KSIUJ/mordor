package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import javax.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileContent
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata

interface FileContentRepository : JpaRepository<FileContent, Long> {
    @Transactional
    fun deleteByFile(id: FileMetadata?)
}
