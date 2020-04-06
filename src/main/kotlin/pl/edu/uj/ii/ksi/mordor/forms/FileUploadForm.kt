package pl.edu.uj.ii.ksi.mordor.forms

import org.springframework.web.multipart.MultipartFile
import pl.edu.uj.ii.ksi.mordor.utilities.validators.file.FileNotEmpty

data class FileUploadForm(
    @get:FileNotEmpty
    var file: MultipartFile? = null
)
