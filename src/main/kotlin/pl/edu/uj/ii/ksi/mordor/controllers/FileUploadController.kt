package pl.edu.uj.ii.ksi.mordor.controllers

import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import pl.edu.uj.ii.ksi.mordor.exceptions.BadRequestException
import pl.edu.uj.ii.ksi.mordor.forms.FileUploadForm
import pl.edu.uj.ii.ksi.mordor.model.DirectoriesTreeNodeEntry
import pl.edu.uj.ii.ksi.mordor.persistence.entities.Permission
import pl.edu.uj.ii.ksi.mordor.persistence.repositories.UserRepository
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryDirectory
import pl.edu.uj.ii.ksi.mordor.services.repository.RepositoryService
import pl.edu.uj.ii.ksi.mordor.services.upload.session.UploadSessionService

@Controller
class FileUploadController(
    private val userRepository: UserRepository,
    private val uploadSessionService: UploadSessionService,
    private val repositoryService: RepositoryService
) {
    private fun getDirectoriesTree(path: String = ""): List<DirectoriesTreeNodeEntry> {
        val entity = repositoryService.getEntity(path)
        return if (entity is RepositoryDirectory) {
            val childEntries = entity
                    .getChildren(false)
                    .filterIsInstance<RepositoryDirectory>()
                    .map { getDirectoriesTree(it.relativePath) }
                    .flatten()
            listOf(DirectoriesTreeNodeEntry(entity.name, childEntries, entity.relativePath))
        } else {
            emptyList()
        }
    }

    @Secured(Permission.UPLOAD_STR)
    @GetMapping("/upload/")
    fun fileUploadPage(): ModelAndView {
        val directoriesTree = getDirectoriesTree()
        return ModelAndView("upload/upload", mapOf(
                "form" to FileUploadForm(),
                "directoriesTree" to directoriesTree
        ))
    }

    @Secured(Permission.UPLOAD_STR)
    @PostMapping("/upload/")
    fun uploadMultipartFile(
        @Valid @ModelAttribute("form") model: FileUploadForm,
        authentication: Authentication,
        result: BindingResult
    ): ModelAndView {
        val username = authentication.name
        val user = userRepository.findByUserName(username)
        if (result.hasErrors()) {
            return ModelAndView("upload/upload", HttpStatus.BAD_REQUEST)
        }
        user?.let {
            val session = uploadSessionService.createFileSession(user)
            val repository = uploadSessionService.getRepositoryService(session)
            for (file in model.files) {
                val mountPath = if (model.mountPath == "/") { "." } else { model.mountPath }
                if (!repositoryService.fileExists(mountPath)) {
                    throw BadRequestException("No directory at chosen path")
                }
                repository.saveFile("$mountPath/${file.originalFilename}", file.inputStream)
            }
            return ModelAndView(RedirectView("/upload/"))
        }
        throw InternalError("No user for username $username")
    }
}
