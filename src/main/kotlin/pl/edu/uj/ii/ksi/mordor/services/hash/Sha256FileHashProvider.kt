package pl.edu.uj.ii.ksi.mordor.services.hash

import com.coremedia.iso.Hex
import java.io.File
import java.nio.file.Files
import java.security.MessageDigest
import org.springframework.stereotype.Service

@Service
class Sha256FileHashProvider : FileHashProvider {
    companion object {
        const val algorithm = "SHA-256"
    }

    override fun calculate(file: File): String {
        val messageDigest = MessageDigest.getInstance(algorithm)
        val bytes = Files.readAllBytes(file.toPath())
        return Hex.encodeHex(messageDigest.digest(bytes))
    }
}