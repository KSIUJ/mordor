package pl.edu.uj.ii.ksi.mordor.services

import java.io.File
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.stereotype.Service

@Service
class ThumbnailExtractorFromPDF : ThumbnailExtractor {
    override fun extract(file: File): ByteArray? {
        val firstPage = PDFRenderer(PDDocument.load(file)).renderImage(0)
        return ThumbnailExtractorFromImage().extract(firstPage)
    }
}
