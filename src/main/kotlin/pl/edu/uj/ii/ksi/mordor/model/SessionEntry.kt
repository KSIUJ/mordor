package pl.edu.uj.ii.ksi.mordor.model

data class SessionEntry(
        val userId: Long,
        val userName: String,
        val sessionId: String
)