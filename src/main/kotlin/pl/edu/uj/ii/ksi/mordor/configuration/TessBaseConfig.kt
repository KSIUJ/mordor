package pl.edu.uj.ii.ksi.mordor.configuration

import org.bytedeco.tesseract.TessBaseAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TessBaseConfig {
    @Bean
    fun tessBaseAPI(): TessBaseAPI {
        val api = TessBaseAPI()
        api.Init("./src/main/resources/tessdata/", "eng")
        api.Init("./src/main/resources/tessdata/", "pol")
        return api
    }
}
