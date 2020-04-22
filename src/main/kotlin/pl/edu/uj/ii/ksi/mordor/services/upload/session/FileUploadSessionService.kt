package pl.edu.uj.ii.ksi.mordor.services.upload.session

import java.awt.print.Pageable
import org.apache.commons.io.monitor.FileEntry
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class FileUploadSessionService {
    fun accept(uploadSession: FileUploadSession) {}

    fun reject(uploadSession: FileUploadSession) {}

    fun getAllFiles(uploadSession: FileUploadSession): List<FileEntry> {
        return ArrayList()
    }

    fun getAllSessions(pageable: Pageable): Page<FileUploadSession> {
        return Page.empty()
    }
}
