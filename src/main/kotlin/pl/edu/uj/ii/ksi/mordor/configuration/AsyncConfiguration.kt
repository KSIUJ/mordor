package pl.edu.uj.ii.ksi.mordor.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableAsync
@EnableScheduling
class AsyncConfiguration {
    @Bean
    fun taskExecutor(): TaskExecutor {
        return SimpleAsyncTaskExecutor()
    }
}
