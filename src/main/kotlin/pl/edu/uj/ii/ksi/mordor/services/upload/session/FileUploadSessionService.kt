package pl.edu.uj.ii.ksi.mordor.services.upload.session

import org.springframework.stereotype.Service

@Service
class FileUploadSessionService {
    // TODO: Implementation
    fun accept(uploadSession: FileUploadSession) {}

    fun reject(uploadSession: FileUploadSession) {}

    fun getAllSessions(): List<FileUploadSession> {
        return ArrayList()
    }
}
