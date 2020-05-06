package pl.edu.uj.ii.ksi.mordor.services

import java.awt.image.BufferedImage
import java.io.File
import java.util.LinkedList
import net.sourceforge.tess4j.Tesseract
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.springframework.stereotype.Service

@Service
class PDFTextExtractor(private val tesseract: Tesseract) : FileTextExtractor {

    override fun extract(file: File, maxLength: Int): String? {
        val extracted = StringBuilder()
        val bufferedImages = formatPDF(file)

        if (!bufferedImages.isEmpty()) {
            for (image in bufferedImages) {
                val text: String? = ImageTextExtractor(tesseract).extractTextFromBufferedImage(image)
                if (text != null) {
                    extracted.append(text)
                }
            }
        }
        return extracted.toString()
    }

    private fun formatPDF(pdfFile: File): LinkedList<BufferedImage?> {
        val bufferedImages = LinkedList<BufferedImage?>()
        val doc: PDDocument = PDDocument.load(pdfFile)
        doc.use {
            for (page in doc.pages) {
                val resources = page.resources
                for (xObjectName in resources.xObjectNames) {
                    val xObject = resources.getXObject(xObjectName)
                    if (xObject is PDImageXObject) {
                        bufferedImages.add(xObject.image)
                    }
                }
            }
        }
        return bufferedImages
    }
}
