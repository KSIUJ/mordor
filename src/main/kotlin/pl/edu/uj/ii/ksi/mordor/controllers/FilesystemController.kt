package pl.edu.uj.ii.ksi.mordor.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import java.nio.file.*
import javax.servlet.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.*
import org.apache.commons.io.*

@Controller
class FilesystemController() {
    lateinit var rootPath: Path

    @Value("\${mordor.root_path}")
    fun setRootPath(rootPath: String) {
        this.rootPath = Paths.get(rootPath);
    }

    fun getRequestPath(request: HttpServletRequest): Path {
        var parts = request.getServletPath().split("/");
        var current = rootPath;
        for (i in 2..(parts.size-1)) {
            var part = parts[i];
            if (part == "." || part == "..")
                throw RuntimeException("invalid path");

            if (part != "")
                current = current.resolve(part);
        }
        return current;
    }

    data class FileEntry (
        val path: String,
        val name: String
    )

    @GetMapping("/file/**")
    fun fileIndex(request: HttpServletRequest): ModelAndView {
        val path = getRequestPath(request);

        if(Files.isDirectory (path)) {
            val stream = Files.newDirectoryStream(path);
            var children = mutableListOf<FileEntry>()

            stream.use {
                for (file in stream) {
                    children.add(FileEntry(
                            rootPath.relativize(file).toString(), file.getFileName().toString()));
                }
            }

            return ModelAndView("tree", "children", children);
        } else {
            return ModelAndView("redirect:/download/" + rootPath.relativize(path).toString());
        }
    }

    @GetMapping("/download/**")
    fun download(request: HttpServletRequest, response: HttpServletResponse) {
        val path = getRequestPath(request);

        response.setContentType("application/octet-stream");

        val stream = Files.newInputStream(path);
        stream.use {
            IOUtils.copy(stream, response.getOutputStream());
        }
        response.flushBuffer();
    }

    @GetMapping("/file/info")
    @ResponseBody
    fun fileInfo(): String {
        return rootPath.toString()
    }

}
