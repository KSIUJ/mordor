package pl.edu.uj.ii.ksi.mordor.services.text.extractor

import com.recognition.software.jdeskew.ImageDeskew
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.LinkedList
import javax.imageio.ImageIO
import net.sourceforge.tess4j.util.ImageHelper
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.bytedeco.tesseract.TessBaseAPI
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class PDFTextExtractor(private val tessBaseAPI: TessBaseAPI) : FileTextExtractor {

    private val maxSkewAngle = 0.05

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    override fun extract(file: File, maxLength: Int): String? {
        val bufferedImages = formatPDF(file)
        if (bufferedImages.isEmpty()) {
            return null
        }

        val extracted = StringBuilder()
        val outputFile = File.createTempFile("temp", "jpg")
        for (image in bufferedImages) {
            if (maxLength >= 0 && extracted.length > maxLength) {
                break
            }

            try {
                ImageIO.write(correctTwisted(image), "jpg", outputFile)
                val text: String? = ImageTextExtractor(tessBaseAPI).extract(outputFile, maxLength)
                if (text != null) {
                    extracted.append(text)
                }
            } catch (e: IOException) {
                logger.error("Could not retrieve text from " + file.absolutePath)
            }
        }
        outputFile.delete()

        return if (maxLength >= 0 && maxLength < extracted.length) {
            extracted.toString().substring(0, maxLength)
        } else {
            extracted.toString()
        }
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

    private fun correctTwisted(image: BufferedImage?): BufferedImage? {
        val imageSkewAngle = ImageDeskew(image).skewAngle
        if (kotlin.math.abs(imageSkewAngle) > maxSkewAngle) {
            return ImageHelper.rotateImage(ImageHelper.convertImageToGrayscale(image), -imageSkewAngle)
        }
        return ImageHelper.convertImageToGrayscale(image)
    }
}
