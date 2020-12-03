package pl.edu.uj.ii.ksi.mordor.configuration

import org.apache.tika.Tika
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TikaConfig {
    @Bean
    fun tika(): Tika {
        return Tika()
    }
}
