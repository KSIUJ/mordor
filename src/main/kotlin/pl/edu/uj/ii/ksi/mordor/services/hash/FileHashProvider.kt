package pl.edu.uj.ii.ksi.mordor.services.hash

import java.io.File

interface FileHashProvider {
    fun calculate(file: File): String
}
