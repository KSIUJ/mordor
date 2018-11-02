package pl.edu.uj.ii.ksi.mordor.services.repository

import org.junit.Test

import org.junit.Assert.*
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import java.nio.file.Path
import java.nio.file.Paths

class RepositoryServiceTest {
    val repositoryService = RepositoryService("/srv/mordor")

    @Test
    fun getAbsolutePath_correct() {
        assertEquals(Paths.get("/srv/mordor/test.txt"), repositoryService.getAbsolutePath("test.txt"))
        assertEquals(Paths.get("/srv/mordor/a/b/c/test.txt"), repositoryService.getAbsolutePath("a/b/c/test.txt"))
    }

    @Test(expected = BadRequestException::class)
    fun getAbsolutePath_outside_of_repository_root() {
        repositoryService.getAbsolutePath("../../etc/passwd")
    }
}