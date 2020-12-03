package pl.edu.uj.ii.ksi.mordor.services.repository

interface RepositoryEntity {
    val name: String

    val relativePath: String

    fun needsMetadata(): Boolean { return false }
}
