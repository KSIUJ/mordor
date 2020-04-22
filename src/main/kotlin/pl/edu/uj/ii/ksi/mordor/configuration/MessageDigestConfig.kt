package pl.edu.uj.ii.ksi.mordor.configuration

import java.security.MessageDigest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessageDigestConfig(@Value("\${mordor.hash.algorithm}") private val hashAlgorithm: String) {
    @Bean
    fun messageDigest(): MessageDigest {
        return MessageDigest.getInstance(hashAlgorithm)
    }
}
