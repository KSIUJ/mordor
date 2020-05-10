package pl.edu.uj.ii.ksi.mordor.forms

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import org.springframework.web.multipart.MultipartFile

data class FileUploadForm(
    @get:NotEmpty.List
    var files: List<MultipartFile> = emptyList(),

    @get:NotBlank
    var mountPath: String = ""
)
