package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.repository.FilmRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RentFilmController {

	private FilmRepository filmRepository;

	public RentFilmController(FilmRepository filmRepository) {
		this.filmRepository = filmRepository;
	}

	@GetMapping("/rent-films/")
	public String getRentFilms(Model model) {
		model.addAttribute("allFilms", filmRepository.findAll());

		return "films-rental";
	}
}
