package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File
import org.apache.tika.Tika

class ThumbnailChainOfResponsibility(private val tika: Tika) : ThumbnailExtractor() {
    override fun extract(file: File): ByteArray? {
        val start = initChain()
        return start.extract(file)
    }

    private fun initChain(): ThumbnailExtractor {
        val te1 = ImageThumbnailExtractor(tika)
        val te2 = PDFThumbnailExtractor(tika)
        te1.next = te2
        return te1
    }
}
