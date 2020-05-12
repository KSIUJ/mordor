package pl.edu.uj.ii.ksi.mordor.services.text.extractor

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.LinkedList
import javax.imageio.ImageIO
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.bytedeco.tesseract.TessBaseAPI
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class BytedecoPDFTextExtractor(private val tessBaseAPI: TessBaseAPI) : FileTextExtractor {

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    override fun extract(file: File, maxLength: Int): String? {
        val extracted = StringBuilder()
        val bufferedImages = formatPDF(file)

        if (!bufferedImages.isEmpty()) {
            val outputfile = File("temp.jpg")
            outputfile.createNewFile()
            for (image in bufferedImages) {
                try {
                    ImageIO.write(image, "jpg", outputfile)
                    val text: String? = BytedecoImageTextExtractor(tessBaseAPI).extract(outputfile, maxLength)
                    if (text != null) {
                        extracted.append(text)
                    }
                } catch (e: IOException) {
                    logger.error("Could not retrieve text from " + file.absolutePath)
                }
            }
            outputfile.delete()
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
