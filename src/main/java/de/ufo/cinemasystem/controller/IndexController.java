
package de.ufo.cinemasystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.repository.FilmRepository;

/**
 * Controller for "/"
 * @author Jannik Schwaß
 * @version 1.0
 */
@Controller
public class IndexController {
    
    private @Autowired FilmRepository films;
    
    /**
     * TODO: actual index
     * @return 
     */
    @GetMapping("/")
    public String getIndexPage(Model m){
        long count = films.count();
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
        }
        m.addAttribute("title", "Startseite");
        return "welcome";
    }
}
