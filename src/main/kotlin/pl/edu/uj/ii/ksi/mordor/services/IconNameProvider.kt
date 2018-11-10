package pl.edu.uj.ii.ksi.mordor.services

import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryEntity

interface IconNameProvider {
    fun getIconName(entity: RepositoryEntity): String
}
