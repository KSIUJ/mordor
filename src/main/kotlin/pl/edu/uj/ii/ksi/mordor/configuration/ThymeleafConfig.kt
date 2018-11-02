package pl.edu.uj.ii.ksi.mordor.configuration

import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ThymeleafConfig {
    @Bean
    fun layoutDialect(): LayoutDialect {
        return LayoutDialect()
    }
}
