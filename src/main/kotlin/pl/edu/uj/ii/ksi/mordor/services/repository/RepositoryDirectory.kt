package pl.edu.uj.ii.ksi.mordor.services.repository

abstract class RepositoryDirectory(override val name: String, override val relativePath: String) : RepositoryEntity {
    abstract fun getChildren(): List<RepositoryEntity>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RepositoryDirectory) return false

        if (relativePath != other.relativePath) return false

        return true
    }

    override fun hashCode(): Int {
        return relativePath.hashCode()
    }
}
