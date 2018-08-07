package pl.edu.uj.ii.ksi.mordor.events

import org.springframework.context.ApplicationEvent
import pl.edu.uj.ii.ksi.mordor.models.entities.User

data class OnEmailVerificationRequestedEvent(
        val user: User
) : ApplicationEvent(user)
