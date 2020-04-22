package pl.edu.uj.ii.ksi.mordor.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Role
import pl.edu.uj.ii.ksi.mordor.persistence.entities.User
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository

@Component
class DevDataLoader(
    @Value("\${mordor.enable_dev_data_loader:false}") private val enabled: Boolean,
    private var userRepository: UserRepository
) : ApplicationRunner {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun run(args: ApplicationArguments?) {
        if (enabled && userRepository.findByUserName("admin") == null) {
            log.warn("Creating admin user: admin:test")
            userRepository.save(User(0, "admin", "{noop}test", "", "", "",
                true, Role.ADMIN))
        }
    }
}
