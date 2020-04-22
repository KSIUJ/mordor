package pl.edu.uj.ii.ksi.mordor.persistence.entities

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import org.hibernate.annotations.CreationTimestamp

@Entity
data class FileUploadSession (
    @Id
    var id: String? = null,

    @CreationTimestamp
    var timestamp: ZonedDateTime,

    @OneToMany(mappedBy = "session", orphanRemoval = true)
    var files: List<FileEntry>?,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    var user: User
)
