package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File

abstract class ThumbnailExtractor {

    val width: Int
        get() = 200

    val height: Int
        get() = 200

    val transparent: Int
        get() = 0x00FFFFFF

    open fun extract(file: File): ByteArray? {
        return null
    }

    open fun canParse(file: File): Boolean {
        return true
    }
}
