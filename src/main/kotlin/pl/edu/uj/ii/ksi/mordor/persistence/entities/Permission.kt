package pl.edu.uj.ii.ksi.mordor.persistence.entities

import org.springframework.security.core.GrantedAuthority

enum class Permission : GrantedAuthority {
    ROLE_READ,
    ROLE_UPLOAD,
    ROLE_WRITE,
    ROLE_LIST_HIDDEN_FILES,
    ROLE_ACCESS_ADMIN_PANEL,
    ROLE_MANAGE_USERS;

    companion object {
        // Compile time constants for annotations
        const val READ_STR = "ROLE_READ"
        const val UPLOAD_STR = "ROLE_UPLOAD"
        const val WRITE_STR = "ROLE_WRITE"
        const val ACCESS_ADMIN_PANEL_STR = "ROLE_ACCESS_ADMIN_PANEL"
        const val LIST_HIDDENFILES_STR = "ROLE_LIST_HIDDEN_FILES"
        const val MANAGE_USERS_STR = "ROLE_MANAGE_USERS"
    }

    override fun getAuthority(): String {
        return name
    }
}
