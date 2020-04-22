package pl.edu.uj.ii.ksi.mordor.services.upload.session

import org.apache.commons.io.monitor.FileEntry
import org.springframework.stereotype.Service

@Service
class FileUploadSessionService {
    fun accept(uploadSession: FileUploadSession) {}

    fun reject(uploadSession: FileUploadSession) {}

    fun getAllFiles(uploadSession: FileUploadSession): List<FileEntry> {
        return ArrayList()
    }

    fun getAllSessions(): List<FileUploadSession> {
        return ArrayList()
    }
}
