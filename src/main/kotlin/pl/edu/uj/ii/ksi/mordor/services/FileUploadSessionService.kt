package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.persistence.entities.FileUploadSession

@Service
class FileUploadSessionService {
    // TODO: Implementation
    fun isAccepted(uploadSession: FileUploadSession): Boolean {
        return true
    }

    fun accept(uploadSession: FileUploadSession) {}

    fun reject(uploadSession: FileUploadSession) {}
}
