package pl.edu.uj.ii.ksi.mordor.persistence.entities

enum class Role(val permissions: List<Permission>) {
    NOBODY(listOf()),
    USER(listOf(Permission.ROLE_READ, Permission.ROLE_UPLOAD)),
    MOD(listOf(Permission.ROLE_READ, Permission.ROLE_UPLOAD, Permission.ROLE_WRITE, Permission.ROLE_LIST_HIDDEN_FILES,
        Permission.ROLE_ACCESS_ADMIN_PANEL)),
    ADMIN(listOf(Permission.ROLE_READ, Permission.ROLE_UPLOAD, Permission.ROLE_WRITE, Permission.ROLE_LIST_HIDDEN_FILES,
        Permission.ROLE_ACCESS_ADMIN_PANEL, Permission.ROLE_MANAGE_USERS)),
    EXTERNAL(listOf())
}
