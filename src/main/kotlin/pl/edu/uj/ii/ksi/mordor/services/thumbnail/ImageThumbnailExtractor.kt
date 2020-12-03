package pl.edu.uj.ii.ksi.mordor.services.thumbnail

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.image.FilteredImageSource
import java.awt.image.ImageFilter
import java.awt.image.RGBImageFilter
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import org.apache.tika.Tika
import org.springframework.stereotype.Service

@Service
class ImageThumbnailExtractor(private val tika: Tika) : ThumbnailExtractor() {
    override fun extract(file: File): ByteArray? {
        return extract(ImageIO.read(file))
    }

    override fun canParse(file: File): Boolean {
        return tika.detect(file).startsWith("image")
    }

    fun extract(image: BufferedImage): ByteArray? {
        val thumbnail = getTransparentScaledImage(image, width, height)
        val bos = ByteArrayOutputStream()
        ImageIO.write(thumbnail, "png", bos)
        return bos.toByteArray()
    }

    private fun getTransparentScaledImage(image: BufferedImage, finalWidth: Int, finalHeight: Int): BufferedImage {
        val scaledWidth = computeScaledWidth(image, finalWidth, finalHeight)
        val scaledHeight = computeScaledHeight(image, finalWidth, finalHeight)

        val scaledImg = BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_RGB)
        val transparentImg = BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB)

        initGraphics2D(scaledImg, finalWidth, finalHeight)
                .drawImage(image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH),
                        0, 0, null)

        initGraphics2D(transparentImg, finalWidth, finalHeight)
                .drawImage(makeColorTransparent(scaledImg, Color(0, 0, 0, 0)),
                        (finalWidth - scaledWidth) / 2, (finalHeight - scaledHeight) / 2,
                        finalWidth, finalHeight, Color(0, 0, 0, 0), null)

        return transparentImg
    }

    private fun computeScaledWidth(image: BufferedImage, finalWidth: Int, finalHeight: Int): Int {
        if (image.width < image.height) {
            return (image.width * finalHeight / image.height)
        }
        return finalWidth
    }

    private fun computeScaledHeight(image: BufferedImage, finalWidth: Int, finalHeight: Int): Int {
        if (image.width > image.height) {
            return (image.height * finalWidth / image.width)
        }
        return finalHeight
    }

    private fun initGraphics2D(image: BufferedImage, finalWidth: Int, finalHeight: Int): Graphics2D {
        val graphics2D = image.createGraphics()
        graphics2D.composite = AlphaComposite.SrcOver
        graphics2D.color = Color(0, 0, 0, 0)
        graphics2D.fillRect(0, 0, finalWidth, finalHeight)
        return graphics2D
    }

    private fun makeColorTransparent(image: BufferedImage, color: Color): Image {
        val markerRGB = color.rgb or -0x1000000

        val filter: ImageFilter = object : RGBImageFilter() {
            override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
                return if (rgb or -0x1000000 == markerRGB) {
                    transparent and rgb
                } else {
                    rgb
                }
            }
        }
        return Toolkit.getDefaultToolkit().createImage(FilteredImageSource(image.source, filter))
    }
}
