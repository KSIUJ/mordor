package pl.edu.uj.ii.ksi.mordor.services.repository

abstract class RepositoryDirectory(override val name: String, override val relativePath: String) : RepositoryEntity {
    abstract fun getChildren(): List<RepositoryEntity>
}
