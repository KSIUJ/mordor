package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File

abstract class ThumbnailExtractor {

    val width: Int
        get() = 200

    val height: Int
        get() = 200

    val transparent: Int
        get() = 0x00FFFFFF

    var next: ThumbnailExtractor?
        get() = this
        set(value) {
            setNextExtractor(value)
        }

    private fun setNextExtractor(extractor: ThumbnailExtractor?) {
        this.next = extractor
    }

    abstract fun extract(file: File): ByteArray?

    open fun canParse(file: File): Boolean {
        return true
    }

    open fun parse(file: File): ByteArray? {
        if (canParse(file)) {
            return extract(file)
        } else if (next != null) {
            return next!!.parse(file)
        }
        return null
    }
}
