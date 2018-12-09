package pl.edu.uj.ii.ksi.mordor.persistence.entities

enum class Role(val permissions: List<Permission>) {
    NOBODY(listOf()),
    USER(listOf(Permission.ROLE_READ, Permission.ROLE_UPLOAD)),
    MOD(listOf(Permission.ROLE_READ, Permission.ROLE_UPLOAD, Permission.ROLE_WRITE, Permission.ROLE_LIST_HIDDEN_FILES)),
    ADMIN(listOf(Permission.ROLE_READ, Permission.ROLE_UPLOAD, Permission.ROLE_WRITE, Permission.ROLE_LIST_HIDDEN_FILES,
        Permission.ROLE_MANAGE_USERS))
}
