package pl.edu.uj.ii.ksi.mordor.persistence.entities

enum class Role(val permissions: List<Permission>) {
    ROLE_NOBODY(listOf()),
    ROLE_USER(listOf(Permission.READ, Permission.UPLOAD)),
    ROLE_MOD(listOf(Permission.READ, Permission.UPLOAD, Permission.WRITE, Permission.LIST_HIDDENFILES)),
    ROLE_ADMIN(listOf(Permission.READ, Permission.UPLOAD, Permission.WRITE, Permission.LIST_HIDDENFILES,
        Permission.MANAGE_USERS));
}
