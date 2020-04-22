package pl.edu.uj.ii.ksi.mordor.services.hash

import java.io.File
import java.nio.file.Files
import java.security.MessageDigest
import org.springframework.stereotype.Service

@Service
class Sha256FileHashProvider(private val messageDigest: MessageDigest) : FileHashProvider {
    override fun calculate(file: File): String {
        val bytes = Files.readAllBytes(file.toPath())
        return messageDigest.digest(bytes).toString()
    }
}
