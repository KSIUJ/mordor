package pl.edu.uj.ii.ksi.mordor.services.upload.session

import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User

@Suppress("NotImplementedDeclaration", "TooManyFunctions")
class FileUploadSessionRepository : PagingAndSortingRepository<FileUploadSession, Pair<User, String>> {
    override fun <S : FileUploadSession?> save(entity: S): S {
        TODO("Not yet implemented")
    }

    override fun findAll(sort: Sort): MutableIterable<FileUploadSession> {
        TODO("Not yet implemented")
    }

    override fun findAll(pageable: Pageable): Page<FileUploadSession> {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableIterable<FileUploadSession> {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Pair<User, String>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll(entities: MutableIterable<FileUploadSession>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun <S : FileUploadSession?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        TODO("Not yet implemented")
    }

    override fun count(): Long {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: MutableIterable<Pair<User, String>>): MutableIterable<FileUploadSession> {
        TODO("Not yet implemented")
    }

    override fun existsById(id: Pair<User, String>): Boolean {
        TODO("Not yet implemented")
    }

    override fun findById(id: Pair<User, String>): Optional<FileUploadSession> {
        TODO("Not yet implemented")
    }

    override fun delete(entity: FileUploadSession) {
        TODO("Not yet implemented")
    }
}
