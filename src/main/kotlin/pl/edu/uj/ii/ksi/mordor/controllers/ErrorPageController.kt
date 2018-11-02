package pl.edu.uj.ii.ksi.mordor.controllers

import javax.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class ErrorPageController(errorAttributes: ErrorAttributes) : AbstractErrorController(errorAttributes) {
    companion object {
        private const val ERROR_PATH = "/error"
    }

    override fun getErrorPath(): String {
        return ERROR_PATH
    }

    @RequestMapping(value = [ERROR_PATH])
    fun errorPage(request: HttpServletRequest): ModelAndView {
        val mav = ModelAndView("error")
        val errorAttrs = getErrorAttributes(request, false)
        mav.addObject("code", errorAttrs.getOrDefault("status", getStatus(request)))
        mav.addObject("error", errorAttrs.getOrDefault("error", ""))
        mav.addObject("debug", errorAttrs.toString())
        return mav
    }
}
