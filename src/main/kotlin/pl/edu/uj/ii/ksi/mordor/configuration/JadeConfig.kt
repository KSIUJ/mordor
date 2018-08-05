package pl.edu.uj.ii.ksi.mordor.configuration

import de.neuland.jade4j.spring.view.JadeViewResolver
import org.springframework.web.servlet.ViewResolver
import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.spring.template.SpringTemplateLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class JadeConfig {
    @Bean
    fun templateLoader(): SpringTemplateLoader {
        val templateLoader = SpringTemplateLoader()
        templateLoader.basePath = "classpath:/templates/"
        templateLoader.encoding = "UTF-8"
        templateLoader.suffix = ".jade"
        return templateLoader
    }

    @Bean
    fun jadeConfiguration(): JadeConfiguration {
        val configuration = JadeConfiguration()
        configuration.isCaching = false
        configuration.templateLoader = templateLoader()
        return configuration
    }

    @Bean
    fun viewResolver(): ViewResolver {
        val viewResolver = JadeViewResolver()
        viewResolver.setConfiguration(jadeConfiguration())
        return viewResolver
    }
}
