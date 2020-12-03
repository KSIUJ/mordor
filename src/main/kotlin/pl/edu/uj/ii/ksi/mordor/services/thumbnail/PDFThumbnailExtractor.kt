package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.io.File
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.tika.Tika
import org.springframework.stereotype.Service

@Service
class PDFThumbnailExtractor(private val tika: Tika) : ThumbnailExtractor() {
    override fun extract(file: File): ByteArray? {
        val doc = PDDocument.load(file)
        doc.use {
            return ImageThumbnailExtractor(tika).extract(PDFRenderer(doc).renderImage(0))
        }
    }

    override fun canParse(file: File): Boolean {
        return tika.detect(file) == "application/pdf"
    }
}
