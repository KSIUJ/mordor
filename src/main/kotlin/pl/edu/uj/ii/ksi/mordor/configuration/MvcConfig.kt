package pl.edu.uj.ii.ksi.mordor.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
@ComponentScan("pl.edu.uj.ii.ksi.mordor.controllers")
class MvcConfig {
    //
}
