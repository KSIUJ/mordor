package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileMetadata
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileEntryRepository
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.FileMetadataRepository
import java.nio.file.Files
import java.nio.file.Path

@Service
class MetadataGarbageCollector(private val entryRepository: FileEntryRepository, private val metadataRepository: FileMetadataRepository) {
    fun collect() {
        metadataRepository.findAll().forEach{metadata -> collectMetadata(metadata)}
    }

    private fun collectMetadata(metadata: FileMetadata?) {
        metadata?.files?.forEach{entry -> collectEntry(entry)}
        metadata?.id?.let {
             if (metadataRepository.findById(it).get().files?.isEmpty()!!) {
                 metadataRepository.deleteById(it)
             }
        }
    }

    private fun collectEntry(entry: FileEntry) {
        if(!Files.exists(Path.of(entry.path))) {
           entryRepository.deleteById(entry.path)
        }
    }
}