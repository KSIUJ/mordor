package pl.edu.uj.ii.ksi.mordor.services

import com.recognition.software.jdeskew.ImageDeskew
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.TesseractException
import net.sourceforge.tess4j.util.ImageHelper
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Service
class ImageTextExtractor(private val tesseract: Tesseract) : FileTextExtractor {

    override fun extract(file: File): String? {
        return extractTextFromBufferedImage(ImageIO.read(file))
    }

    private fun correctTwisted(image: BufferedImage?): BufferedImage? {
        val imageSkewAngle = ImageDeskew(image).skewAngle
        if (imageSkewAngle < -0.05 || imageSkewAngle > 0.05) {
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
            e.printStackTrace()
        }
        return null
    }

}