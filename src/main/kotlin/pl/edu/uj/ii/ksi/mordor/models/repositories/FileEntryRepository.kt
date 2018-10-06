package pl.edu.uj.ii.ksi.mordor.models.repositories

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.uj.ii.ksi.mordor.models.entities.FileEntry

interface FileEntryRepository : JpaRepository<FileEntry, String>
