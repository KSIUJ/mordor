package pl.edu.uj.ii.ksi.mordor.utilities.validators.file

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class FileNotEmptyValidatorForMultipartFile : ConstraintValidator<FileNotEmpty, MultipartFile> {

    override fun isValid(value: MultipartFile?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return false
        }
        return value.size > 0 && value.name.isNotEmpty()
    }
}
