package pl.edu.uj.ii.ksi.mordor.utilities.validators.file

import javax.validation.Constraint
import kotlin.reflect.KClass

@Constraint(validatedBy = [FileNotEmptyValidatorForMultipartFile::class])
@Target(AnnotationTarget.FIELD,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.FILE,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)

annotation class FileNotEmpty (
    val message: String = "You have to upload a file",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
)
