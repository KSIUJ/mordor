package pl.edu.uj.ii.ksi.mordor.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import java.nio.file.*
import javax.servlet.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.*
import org.apache.commons.io.*
import java.util.*

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
        val name: String,
        val iconName: String
    )

    @GetMapping("/file/**")
    fun fileIndex(request: HttpServletRequest): ModelAndView {
        val path = getRequestPath(request);
        val relativePath = rootPath.relativize(path).toString();

        if(Files.isDirectory (path)) {
            if (!request.getServletPath().endsWith("/"))
               return ModelAndView("redirect:" + request.getServletPath() + "/")

            val stream = Files.newDirectoryStream(path);
            var children = mutableListOf<FileEntry>()

            stream.use {
                for (file in stream) {
                    children.add(FileEntry(
                            rootPath.relativize(file).toString(), file.getFileName().toString(),
                            getIconName(file)));
                }
            }

            val sortedChildren = children.sortedWith(compareBy({it.iconName != "folder"}, {it.name}));

            return ModelAndView("tree",
               mapOf("children" to sortedChildren,
                     "path" to relativePath));
        } else {
            return ModelAndView("redirect:/download/" + rootPath.relativize(path).toString());
        }
    }

    @GetMapping("/download/**")
    fun download(request: HttpServletRequest, response: HttpServletResponse) {
        val path = getRequestPath(request);

        response.addHeader("X-Content-Type-Options", "nosniff");
        response.setContentType(getMimeForPath(path.toString()));

        val stream = Files.newInputStream(path);
        stream.use {
            IOUtils.copy(stream, response.getOutputStream());
        }
        response.flushBuffer();
    }

    fun getIconName(path: Path): String {
        if(Files.isDirectory (path)) {
            return "folder";
        }

        val exts = mapOf(
           ".pdf" to "file-pdf"
        )

        for ((ext, icon) in exts) {
            if (path.toString().toLowerCase().endsWith(ext)) {
                return icon;
            }
        }

        return "file";
    }

    fun getMimeForPath(path: String): String {
        // careful about XSS!
        val exts = mapOf(
           ".pdf" to "application/pdf",
           ".png" to "image/png",
           ".jpg" to "image/jpeg",
           ".jpeg" to "image/jpeg"
        )

        for ((ext, mime) in exts) {
            if (path.toLowerCase().endsWith(ext)) {
                return mime;
            }
        }

        return "application/octet-stream";
    }
    
    @GetMapping("/file/info")
    @ResponseBody
    fun fileInfo(): String {
        return rootPath.toString()
    }

}
