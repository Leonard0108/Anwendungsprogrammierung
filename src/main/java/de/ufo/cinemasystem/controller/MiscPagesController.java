/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ufo.cinemasystem.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Spring MVC controller for misc pages and documents.
 * @author Jannik
 */
@Controller
public class MiscPagesController {
    
    /**
     * Get endpoint method for our robots.txt
     * @param m model
     * @param response http response, used to set cache headers
     * @return robots.txt template name
     */
    @GetMapping(value = "/robots.txt",produces = "text/plain")
    public String robotsTxt(Model m, HttpServletResponse response){
        LocalDateTime now = LocalDateTime.now();
        int week = ViewProgramController.getWeekOfYear(now);
        LocalDateTime nextNow = now.plusDays(7);
        int nextWeek = ViewProgramController.getWeekOfYear(nextNow);
        m.addAttribute("year", now.getYear());
        m.addAttribute("nextYear", now.getYear());
        m.addAttribute("week", week);
        m.addAttribute("nextWeek", nextWeek);
        //end of week
        response.setDateHeader("Expires", ViewProgramController.getEndWeekDateTime(now).toInstant(ZonedDateTime.now().getOffset()).toEpochMilli());
        response.setHeader("Cache-Control", "private,max-age=604800, must-revalidate");
        return "robots.txt";
    }
    
    /**
     * Spring MVC endpoint of the imprint
     * @param m model
     * @return "imprint"
     */
    @GetMapping("/imprint")
    public String getImprint(Model m){
        m.addAttribute("title", "Impressum");
        return "imprint";
    }
    
    /**
     * Spring MVC endpoint of the privacy policy
     * @param m model
     * @return "privacy"
     */
    @GetMapping("/privacy")
    public String getPrivacyPolicy(Model m){
        m.addAttribute("title", "Datenschutzrichtlinie");
        return "privacy";
    }
    
}
