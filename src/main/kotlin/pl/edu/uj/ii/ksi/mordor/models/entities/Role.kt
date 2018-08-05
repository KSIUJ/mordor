package pl.edu.uj.ii.ksi.mordor.models.entities

enum class Role(val permissions: List<Permission>) {
    ROLE_NOBODY(listOf()),
    ROLE_USER(listOf(Permission.READ, Permission.UPLOAD)),
    ROLE_MOD(listOf(Permission.READ, Permission.UPLOAD, Permission.WRITE)),
    ROLE_ADMIN(listOf(Permission.READ, Permission.UPLOAD, Permission.WRITE, Permission.MANAGE_USERS));
}
