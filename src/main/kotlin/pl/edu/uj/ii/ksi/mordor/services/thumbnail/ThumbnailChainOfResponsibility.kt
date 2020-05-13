package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File
import org.apache.tika.Tika
import org.springframework.stereotype.Service

@Service
class ThumbnailChainOfResponsibility(private val tika: Tika) : ThumbnailExtractor() {

    override fun parse(file: File): ByteArray? {
        val start = initChain()
        return start.parse(file)
    }

    private fun initChain(): ThumbnailExtractor {
        val extractors = ArrayList<ThumbnailExtractor>()
        extractors.add(ImageThumbnailExtractor(tika))
        extractors.add(PDFThumbnailExtractor(tika))

        for (i in (extractors.size - 2) downTo 0) {
            extractors[i].addNext(extractors[i + 1])
        }
        return extractors[0]
    }
}
