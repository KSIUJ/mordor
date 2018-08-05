package pl.edu.uj.ii.ksi.mordor

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import pl.edu.uj.ii.ksi.mordor.models.entities.Role
import pl.edu.uj.ii.ksi.mordor.models.entities.User
import pl.edu.uj.ii.ksi.mordor.models.repositories.UserRepository

@Component
class DevDataLoader : ApplicationRunner {
    @Autowired
    lateinit var userRepository: UserRepository

    val log: Logger = LoggerFactory.getLogger(this.javaClass)


    override fun run(args: ApplicationArguments?) {
        if (userRepository.findByUsername("admin") == null) {
            log.warn("Creating admin user: admin:test")
            userRepository.save(User(0, "admin", "{noop}test", "", "", "",
                    true, Role.ROLE_ADMIN))
        }
    }

}
