package pl.edu.uj.ii.ksi.mordor.services

import com.recognition.software.jdeskew.ImageDeskew
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.TesseractException
import net.sourceforge.tess4j.util.ImageHelper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService

@Service
class ImageTextExtractor(private val tesseract: Tesseract) : FileTextExtractor {

    val maxSkewAngle = 0.05

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryService::class.java)
    }

    override fun extract(file: File, maxLength: Int): String? {
        return extractTextFromBufferedImage(ImageIO.read(file))
    }

    private fun correctTwisted(image: BufferedImage?): BufferedImage? {
        val imageSkewAngle = ImageDeskew(image).skewAngle
        if (kotlin.math.abs(imageSkewAngle) > maxSkewAngle) {
            return ImageHelper.rotateImage(ImageHelper.convertImageToGrayscale(image), -imageSkewAngle)
        }
        return ImageHelper.convertImageToGrayscale(image)
    }

    fun extractTextFromBufferedImage(image: BufferedImage?): String? {
        try {
            return tesseract.doOCR(correctTwisted(image))
                    .replace("\\n{2,}", "\n")
                    .trim { c -> c <= ' ' }
        } catch (e: TesseractException) {
            logger.error("Tesseract is unable do process OCR on image", e)
        }
        return null
    }
}
