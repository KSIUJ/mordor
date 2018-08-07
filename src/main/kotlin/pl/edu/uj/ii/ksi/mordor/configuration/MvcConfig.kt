package pl.edu.uj.ii.ksi.mordor.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.context.annotation.Bean


@Configuration
@EnableWebMvc
@ComponentScan("pl.edu.uj.ii.ksi.mordor.controllers")
class MvcConfig {
    @Bean
    fun layoutDialect(): LayoutDialect {
        return LayoutDialect()
    }
}
