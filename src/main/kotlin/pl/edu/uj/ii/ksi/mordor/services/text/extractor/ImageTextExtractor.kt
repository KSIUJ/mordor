package pl.edu.uj.ii.ksi.mordor.services.text.extractor

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOInvalidTreeException
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.print.attribute.ResolutionSyntax.DPI
import org.bytedeco.leptonica.global.lept.pixRead
import org.bytedeco.tesseract.TessBaseAPI

class ImageTextExtractor(private val tessBaseAPI: TessBaseAPI) : FileTextExtractor {
    private val inch2milli = 2.54 * 10

    override fun extract(file: File, maxLength: Int): String? {
        preventLowDPI(file)
        tessBaseAPI.SetImage(pixRead(file.absolutePath))

        val res = tessBaseAPI.GetUTF8Text().string.trimIndent()
        if (maxLength < 0) {
            return res
        }
        return res.take(maxLength)
    }

    @Throws(IOException::class)
    private fun preventLowDPI(output: File) {
        val gridImage = ImageIO.read(output)
        output.delete()
        val formatName = "png"
        val iw = ImageIO.getImageWritersByFormatName(formatName)
        while (iw.hasNext()) {
            val writer = iw.next()
            val writeParam = writer.defaultWriteParam
            val typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB)
            val metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam)
            if (metadata.isReadOnly || !metadata.isStandardMetadataFormatSupported) {
                continue
            }
            setDPI(metadata)
            val stream = ImageIO.createImageOutputStream(output)
            stream.use {
                writer.output = stream
                writer.write(metadata, IIOImage(gridImage, null, metadata), writeParam)
            }
        }
    }

    @Throws(IIOInvalidTreeException::class)
    private fun setDPI(metadata: IIOMetadata) {
        // for PMG, it's dots per millimeter
        val dotsPerMilli: Double = 1.0 * DPI / inch2milli
        val horiz = IIOMetadataNode("HorizontalPixelSize")
        horiz.setAttribute("value", dotsPerMilli.toString())
        val vert = IIOMetadataNode("VerticalPixelSize")
        vert.setAttribute("value", dotsPerMilli.toString())
        val dim = IIOMetadataNode("Dimension")
        dim.appendChild(horiz)
        dim.appendChild(vert)
        val root = IIOMetadataNode("javax_imageio_1.0")
        root.appendChild(dim)
        metadata.mergeTree("javax_imageio_1.0", root)
    }
}
