package pl.edu.uj.ii.ksi.mordor.models.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.models.entities.FileMetadata

interface FileMetadataRepository : JpaRepository<FileMetadata, String>
