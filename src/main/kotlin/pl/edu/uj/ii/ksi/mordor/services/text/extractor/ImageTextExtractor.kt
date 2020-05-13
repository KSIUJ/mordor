package pl.edu.uj.ii.ksi.mordor.services.text.extractor

import java.io.File
import org.bytedeco.leptonica.global.lept.pixRead
import org.bytedeco.tesseract.TessBaseAPI

class ImageTextExtractor(private val tessBaseAPI: TessBaseAPI) : FileTextExtractor {

    override fun extract(file: File, maxLength: Int): String? {
        tessBaseAPI.SetImage(pixRead(file.absolutePath))

        val res = tessBaseAPI.GetUTF8Text().string.trimIndent()
        if (maxLength < 0) {
            return res
        }
        return res.take(maxLength)
    }
}
