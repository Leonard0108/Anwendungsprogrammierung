/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import de.ufo.cinemasystem.models.CinemaShowService;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;

/**
 *
 * @author Jannik
 */
@Controller
public class ViewProgramController {

	private CinemaShowRepository cinemaShowRepository;

	private CinemaShowService cinemaShowService;

	private CinemaHallRepository cinemaHallRepository;

	private FilmRepository filmRepository;

	public ViewProgramController(CinemaShowRepository cinemaShowRepository, CinemaShowService cinemaShowService,
								 CinemaHallRepository cinemaHallRepository, FilmRepository filmRepository) {
		this.cinemaShowRepository = cinemaShowRepository;
		this.cinemaShowService = cinemaShowService;
		this.cinemaHallRepository = cinemaHallRepository;
		this.filmRepository = filmRepository;
	}
        
        @GetMapping("/current-films")
        public String getCurrentWeekProgram(Model m){
            LocalDateTime now = LocalDateTime.now();
			return "redirect:/current-films/" + now.getYear() + "/" + AdditionalDateTimeWorker.getWeekOfYear(now);
        }

    /**
     * todo: where rights check?
     * @param year
     * @param week
     * @param m 
     * @return  
     */
    @GetMapping("/current-films/{year}/{week}")
    public String getCurrentProgram(@PathVariable int year, @PathVariable int week , Model m) {

		LocalDateTime startDateTime = AdditionalDateTimeWorker.getStartWeekDateTime(year, week);
		LocalDate dayDate;
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		List<CinemaShowDayEntry> oneWeekCinemaShows = new ArrayList<>();

		int[] nextYearWeek = AdditionalDateTimeWorker.nextWeek(year, week);
		int[] lastYearWeek = AdditionalDateTimeWorker.lastWeek(year, week);

		m.addAttribute("lastWeekRangeFormat", AdditionalDateTimeWorker.getWeekRangeFormat(lastYearWeek[0], lastYearWeek[1]));
		m.addAttribute("lastYear", lastYearWeek[0]);
		m.addAttribute("lastWeek", lastYearWeek[1]);

		m.addAttribute("currentWeekRangeFormat", AdditionalDateTimeWorker.getWeekRangeFormat(year, week));

		m.addAttribute("nextWeekRangeFormat", AdditionalDateTimeWorker.getWeekRangeFormat(nextYearWeek[0], nextYearWeek[1]));
		m.addAttribute("nextYear", nextYearWeek[0]);
		m.addAttribute("nextWeek", nextYearWeek[1]);

		// TODO: Sortierung nach Zeit an einem Tag korrekt implementieren
		// TODO: effizienter umsetzen:
		// Alle Wochentage einzeln behandeln
		for(int i = 1; i <= 7; i++) {
			dayDate = startDateTime.with(weekFields.dayOfWeek(), i).toLocalDate();
			oneWeekCinemaShows.add(
				new CinemaShowDayEntry(dayDate, this.cinemaShowRepository.findCinemaShowsOnDay(dayDate))
			);
		}
		m.addAttribute("oneWeekCinemaShows", oneWeekCinemaShows);
		m.addAttribute("allCinemaHalls", cinemaHallRepository.findAll());
		m.addAttribute("allFilms", filmRepository.findAll());

		//System.out.println("Start der Woche: " + getStartWeekDateTime(year, week));
		//System.out.println("Ende der Woche: " + getEndWeekDateTime(year, week));

		return "current-films";
	}

	@PostMapping("/current-films/{year}/{week}")
	public String postNewProgram(@PathVariable int year, @PathVariable int week,
								 @RequestParam("film") Long film, @RequestParam("room") Long room,
								 //ChatGPT 3.5
								 // Promt: Wie kann ich aus einem input Feld vom type="datetime-local ein LocalDateTime Objekt machen?
								 @RequestParam("addTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime addTime,
								 Model m) {

		Optional<Film> optFilmInst = filmRepository.findById(film);
		Optional<CinemaHall> optRoomInst = cinemaHallRepository.findById(room);
		if(optFilmInst.isEmpty()) {
			// TODO Fehlerbehandlung
			return "redirect:/current-films/{year}/{week}";
		}
		if(optRoomInst.isEmpty()) {
			// TODO Fehlerbehandlung
			return "redirect:/current-films/{year}/{week}";
		}
		// TODO: Prüfe ob sich Events oder Filme überlappen, wenn ja Abbruch und Fehler
		// Erstelle neue Vorführung
		cinemaShowService.createCinemaShow(addTime, Money.of(9.99, EURO), optFilmInst.get(), optRoomInst.get());

		return "redirect:/current-films/{year}/{week}";
	}

	@GetMapping("/cinema-shows/{id}")
	public String detailCinemaShow(@PathVariable Long id, Model m) {
		Optional<CinemaShow> optionalCinemaShow = cinemaShowRepository.findById(id);
		if(optionalCinemaShow.isEmpty()) {
			// TODO Fehlerbehandlung
			return "redirect:/current-films";
		}
		CinemaShow cinemaShow = optionalCinemaShow.get();

		m.addAttribute("cinemaShow", cinemaShow);
		m.addAttribute("allFilms", filmRepository.findAll());

		return "film-detail";
	}

	@PostMapping("/cinema-shows/{id}/edit")
	public String editCinemaShow(@PathVariable Long id,
								 @RequestParam("film") Long film,
								 @RequestParam("editTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime editTime,
								 Model m) {
		Optional<Film> optFilmInst = filmRepository.findById(film);
		if(optFilmInst.isEmpty()) {
			// TODO Fehlerbehandlung
			return "redirect:/current-films/{year}/{week}";
		}

		cinemaShowService.update(id)
			.setStartDateTime(editTime)
			.setFilm(optFilmInst.get())
			.save();

		return "redirect:/cinema-shows/{id}";
	}

	@PostMapping("/cinema-shows/{id}/delete")
	public String deleteCinemaShow(@PathVariable Long id, Model m) {
		Optional<CinemaShow> optionalCinemaShow = cinemaShowRepository.findById(id);
		if(optionalCinemaShow.isEmpty()) {
			// TODO Fehlerbehandlung
			return "redirect:/current-films";
		}
		cinemaShowService.deleteCinemaShow(optionalCinemaShow.get());

		return "redirect:/current-films";
	}

	public static class CinemaShowDayEntry {
		private Streamable<CinemaShow> cinemaShows;
		private String dayDateHeadline;

		/**
		 *
		 * @param dayDate Datum, für alle Veranstaltungen welche an dem Tag laufen (Startzeit).
		 * @param cinemaShows alle Veranstaltungen an einem Tag
		 */
		public CinemaShowDayEntry(LocalDate dayDate, Streamable<CinemaShow> cinemaShows) {
			// Verhindert Fehler bei nicht vorhandenen Kino-Veranstaltungen an dem Tag
			if(cinemaShows == null) cinemaShows = Streamable.empty();
			this.cinemaShows = cinemaShows;
			this.dayDateHeadline = AdditionalDateTimeWorker.getDayFormat(dayDate);
		}

		public Streamable<CinemaShow> getCinemaShows() {
			return this.cinemaShows;
		}

		/**
		 * @return Erhalte Datums-Überschrift-Eintrag
		 */
		public String getDayDateHeadline() {
			return this.dayDateHeadline;
		}
	}
}
