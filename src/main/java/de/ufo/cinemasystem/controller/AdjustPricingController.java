package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.repository.FilmRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdjustPricingController {
	private FilmRepository filmRepository;

	public AdjustPricingController(FilmRepository filmRepository) {
		this.filmRepository = filmRepository;
	}

	@GetMapping("/manage/pricing")
	String getAdjustPricing(Model m){
		List<Film> allFilms = filmRepository.findAll().toList();
		m.addAttribute("allFilms", allFilms);
		m.addAttribute("title", "Preisgestaltung");

		return "adjust-pricing";
	}

	@GetMapping(value = "/manage/pricing", params = "ChangePriceOf")
	String getPriceOf(Model m, @RequestParam("ChangePriceOf") Film /* Film oder Snack*/ changePriceOf){
		m.addAttribute("title", "Preisgestaltung");
		return "adjust-pricing";
	}


}
