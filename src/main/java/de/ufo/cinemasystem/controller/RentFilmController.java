package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.additionalfiles.YearWeekEntry;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.repository.FilmRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RentFilmController {

	private FilmRepository filmRepository;

	public RentFilmController(FilmRepository filmRepository) {
		this.filmRepository = filmRepository;
	}

	@GetMapping("/rent-films")
	public String getRentFilms(Model model) {
		model.addAttribute("allFilms", filmRepository.findAll());

		return "films-rental";
	}

	@GetMapping("/rent-films/{film}")
	public String getRentFilm(Model model, @PathVariable Film film) {
		final int futureRentWeeks = 8;
		List<YearWeekEntry> allRangeYearWeeks = new ArrayList<>();
		YearWeekEntry nextWeek = YearWeekEntry.getNowYearWeek().nextWeek();
		Optional<YearWeekEntry> firstFilmRentWeek = film.getFirstRentWeek();
		Optional<YearWeekEntry> lastFilmRentWeek = film.getLastRentWeek();

		// erste Woche, welche in der Tabelle angezeigt werden soll:
		// Fall 1: Es gibt noch keine Leihwochen bzw. die Leihwochen liegen in der Zukunft, dann soll ab der nächsten Woche alles angezeigt werden
		// Fall 2: einige Leihwochen liegen bereits in der Vergangenheit bzw. in der aktuellen Woche, so soll die erste Leihwoche des Films ausgewählt werden
		YearWeekEntry currentWeek = (firstFilmRentWeek.isEmpty() || nextWeek.compareTo(firstFilmRentWeek.get()) <= 0) ?
			nextWeek : firstFilmRentWeek.get();

		// füge alle Wochen von der firstWeek bis zur aktuellen Woche der Liste hinzu
		while(currentWeek.compareTo(nextWeek) < 0) {
			allRangeYearWeeks.add(currentWeek);
			currentWeek = currentWeek.nextWeek();
		}

		// füge futureRentWeeks der Liste hinzu, somit sind Filme aktuell 8 Wochen im voraus ausleihbar
		for(int i = 0; i < futureRentWeeks; i++) {
			allRangeYearWeeks.add(currentWeek);
			currentWeek = currentWeek.nextWeek();
		}

		model.addAttribute("film", film);
		model.addAttribute("allRangeYearWeeks", allRangeYearWeeks);

		return "film-rental-detail";
	}
}
