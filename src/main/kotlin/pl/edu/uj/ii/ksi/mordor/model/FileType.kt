package pl.edu.uj.ii.ksi.mordor.model

enum class FileType(val previewType: String) {
    IMAGE("IMAGE"),
    PAGE("PAGE"),
    TOO_LARGE("TOO_LARGE"),
    CODE("CODE")
}