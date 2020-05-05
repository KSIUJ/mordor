package pl.edu.uj.ii.ksi.mordor.configuration

import net.sourceforge.tess4j.Tesseract
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TesseractConfiguration {
    @Bean
    fun tesseract(): Tesseract {
        val tesseract = Tesseract()
        tesseract.setDatapath("./src/main/resources/")
        tesseract.setLanguage("pol+eng")
        return tesseract
    }
}

