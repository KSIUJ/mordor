package pl.edu.uj.ii.ksi.mordor.services

import java.io.File

interface ThumbnailExtractor {

    val width: Int
        get() = 200

    val height: Int
        get() = 200

    val transparent: Int
        get() = 0x00FFFFFF

    fun extract(file: File): ByteArray?
}
