
package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for "/"
 * @author Jannik Schwaß
 * @version 1.0
 */
@Controller
public class IndexController {
    
    private @Autowired FilmRepository films;
    private @Autowired CinemaShowRepository csRepo;
    
    /**
     * index.html
     * @param m
     * @return "welcome"
     */
    @GetMapping("/")
    public String getIndexPage(Model m){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = AdditionalDateTimeWorker.getEndWeekDateTime(now.plusDays(7));
        List<CinemaShow> options = csRepo.findCinemaShowsInPeriodOfTime(now, end).toList();
        ArrayList<Film> defcopy = new ArrayList<>(3);
        for(CinemaShow c:options){
            if(!defcopy.contains(c.getFilm())){
                if(defcopy.size() == 3){
                    m.addAttribute("hasMore", true);
                    break;
                }
                defcopy.add(c.getFilm());
            }
        }
        /*
        List<Film> toList = films.findAll().toList();
        ArrayList<Film> defcopy = new ArrayList<>(3);
        for(int i = 0; i < toList.size();i++){
        defcopy.add(toList.get(i));
        if(i == 2){
        break;
        }
        }
        m.addAttribute("filmList", defcopy.toArray());
        
        if(count >= 4){
            m.addAttribute("hasMore", true);
        }*/
        m.addAttribute("filmList", defcopy.toArray());
        m.addAttribute("title", "Startseite");
        return "welcome";
    }
}
