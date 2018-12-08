package pl.edu.uj.ii.ksi.mordor.exceptions

import java.lang.RuntimeException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException(val path: String) : RuntimeException("Resource not found: $path")
