package pl.edu.uj.ii.ksi.mordor.services

import org.springframework.stereotype.Service
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


@Service
class ThumbnailExtractorFromImage : ThumbnailExtractor {
    override fun extract(file: File): ByteArray? {
        return extract(ImageIO.read(file))
    }

    fun extract(originalImage: BufferedImage): ByteArray? {
        val thumbnail = getTransparentScaledImage(originalImage, width, height)
        val bos = ByteArrayOutputStream()
        ImageIO.write(thumbnail, "png", bos)
        return bos.toByteArray()
    }

    private fun getTransparentScaledImage(originalImage: BufferedImage, finalWidth: Int, finalHeight: Int): BufferedImage {
        val scaledWidth = computeScaledWidth(originalImage, finalWidth, finalHeight)
        val scaledHeight = computeScaledHeight(originalImage, finalWidth, finalHeight)

        val scaledImg = BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_RGB)
        val transparentImg = BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB)

        initGraphics2D(scaledImg, finalWidth, finalHeight)
                .drawImage(originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH),
                        0, 0, null)

        initGraphics2D(transparentImg, finalWidth, finalHeight)
                .drawImage(makeColorTransparent(scaledImg, Color(0, 0, 0, 0)),
                        (finalWidth - scaledWidth) / 2, (finalHeight - scaledHeight) / 2,
                        finalWidth, finalHeight, Color(0, 0, 0, 0), null)

        return transparentImg
    }

    private fun computeScaledWidth(originalImage: BufferedImage, finalWidth: Int, finalHeight: Int): Int {
        if (originalImage.width < originalImage.height) {
            return (originalImage.width * finalHeight / originalImage.height)
        }
        return finalWidth
    }

    private fun computeScaledHeight(originalImage: BufferedImage, finalWidth: Int, finalHeight: Int): Int {
        if (originalImage.width > originalImage.height) {
            return (originalImage.height * finalWidth / originalImage.width)
        }
        return finalHeight
    }

    private fun initGraphics2D(img: BufferedImage, finalWidth: Int, finalHeight: Int): Graphics2D {
        val gf = img.createGraphics()
        gf.composite = AlphaComposite.SrcOver
        gf.color = Color(0, 0, 0, 0)
        gf.fillRect(0, 0, finalWidth, finalHeight)
        return gf
    }

    private fun makeColorTransparent(image: BufferedImage, color: Color): Image {
        val markerRGB = color.rgb or -0x1000000

        val filter: ImageFilter = object : RGBImageFilter() {
            override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
                return if (rgb or -0x1000000 == markerRGB) {
                    0x00FFFFFF and rgb
                } else {
                    rgb
                }
            }
        }
        return Toolkit.getDefaultToolkit().createImage(FilteredImageSource(image.source, filter))
    }
}
