package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.tika.Tika
import org.springframework.stereotype.Service

@Service
class PDFThumbnailExtractor(private val tika: Tika) : ThumbnailExtractor() {
    override fun extract(file: File): ByteArray? {
        val firstPage = PDFRenderer(PDDocument.load(file)).renderImage(0)
        return ImageThumbnailExtractor(tika).extract(firstPage)
    }

    override fun canParse(file: File): Boolean {
        return tika.detect(file) == "application/pdf"
    }
}
