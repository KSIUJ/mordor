package pl.edu.uj.ii.ksi.mordor.services.hash

import java.io.File
import java.nio.file.Files
import java.security.MessageDigest
import org.springframework.stereotype.Service

@Service
class Sha256FileHashProvider : FileHashProvider {
    companion object {
        const val algorithm = "SHA3-256"
    }

    override fun calculate(file: File): String {
        val messageDigest = MessageDigest.getInstance(algorithm)
        val bytes = Files.readAllBytes(file.toPath())
        return messageDigest.digest(bytes).toString()
    }
}
