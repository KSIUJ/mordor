package pl.edu.uj.ii.ksi.mordor.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class BadRequestException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
}
