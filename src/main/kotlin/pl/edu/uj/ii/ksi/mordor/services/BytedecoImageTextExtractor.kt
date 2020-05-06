package pl.edu.uj.ii.ksi.mordor.services


import org.bytedeco.tesseract.TessBaseAPI
import org.bytedeco.leptonica.global.lept.pixRead

import java.io.File

class BytedecoImageTextExtractor(private val tessBaseAPI: TessBaseAPI) : FileTextExtractor{

    override fun extract(file: File, maxLength: Int): String? {
        tessBaseAPI.SetImage(pixRead(file.absolutePath))

        val res = tessBaseAPI.GetUTF8Text().string.trimIndent()

        return res.substring(0, kotlin.math.min(maxLength, res.length))
    }
}