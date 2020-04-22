package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import javax.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository

import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileThumbnail

interface FileThumbnailRepository : JpaRepository<FileThumbnail, Long> {
    @Transactional
    fun deleteByFile(id: FileMetadata?)
}
