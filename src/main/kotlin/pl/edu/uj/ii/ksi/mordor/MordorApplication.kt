package pl.edu.uj.ii.ksi.mordor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MordorApplication

fun main(args: Array<String>) {
    runApplication<MordorApplication>(*args)
}
