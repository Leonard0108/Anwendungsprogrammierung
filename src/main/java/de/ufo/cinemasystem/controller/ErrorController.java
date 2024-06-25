
package de.ufo.cinemasystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * Controller that handles errors in the application.
 * @author Jannik Schwaß
 * @version 1.0
 */
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController{
    
    /**
     * 
     * handle an error.
     * @param m model
     * @param request the failed request
     * @return "error"
     */
    @RequestMapping(value = {"/error"})
    public String handleError(Model m, HttpServletRequest request){
        Throwable thrown = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
        String trace = null;
        if (thrown != null) {
            try (StringWriter helper = new StringWriter(); PrintWriter helpWriter = new PrintWriter(helper)) {
                thrown.printStackTrace(helpWriter);
                trace = helper.toString();
            } catch (IOException ex) {
                throw new InternalError("huh? I/O Error moving strings in RAM?!");
            }
        } else {
            trace = "N/A";
        }
        String reasonPhrase = null;
        if (request.getAttribute("jakarta.servlet.error.status_code") != null) {
            reasonPhrase = org.springframework.http.HttpStatus.valueOf((Integer) request.getAttribute("jakarta.servlet.error.status_code")).getReasonPhrase();
            m.addAttribute("title", "Fehler");
            m.addAttribute("status", request.getAttribute("jakarta.servlet.error.status_code"));
        } else {
            //direct invocation of /error
            reasonPhrase = org.springframework.http.HttpStatus.OK.getReasonPhrase();
            m.addAttribute("title", "Alles Okay");
            m.addAttribute("status", 200);
        }
        
        m.addAttribute("stacktrace", trace);
        m.addAttribute("failedPath", request.getAttribute("jakarta.servlet.error.request_uri"));
        m.addAttribute("statusDescription", reasonPhrase);
        return "error";
    }
}
