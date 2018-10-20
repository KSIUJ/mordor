package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileThumbnail

interface FileThumbnailRepository : JpaRepository<FileThumbnail, Long>
