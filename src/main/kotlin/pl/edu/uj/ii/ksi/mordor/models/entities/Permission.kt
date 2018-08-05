package pl.edu.uj.ii.ksi.mordor.models.entities

import org.springframework.security.core.GrantedAuthority

enum class Permission : GrantedAuthority {
    READ,
    UPLOAD,
    WRITE,
    MANAGE_USERS;

    override fun getAuthority(): String {
        return name
    }
}
