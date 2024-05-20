/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.CinemaShowService;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
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
import java.time.format.DateTimeFormatter;
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
			return "redirect:/current-films/" + now.getYear() + "/" + getWeekOfYear(now);
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

		LocalDateTime startDateTime = getStartWeekDateTime(year, week);
		LocalDate dayDate;
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		List<CinemaShowDayEntry> oneWeekCinemaShows = new ArrayList<>();

		m.addAttribute("weekRangeFormat", getWeekRangeFormat(year, week));
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

		return "current-films-renderer";
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
			this.dayDateHeadline = getDayFormat(dayDate);
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

	// Helper Methoden
	// TODO: Später in einen Service verschieben

	/**
	 * @return max. Anzahl an Kalenderwochen, welches ein Jahr hat.
	 * Es zählen auch die erste und die letzte Woche, auch wenn diese nicht vollständig in dem Jahr liegen.
	 */
	public static int getMaxYearWeeks(int year) {
		return getWeekOfYear(LocalDateTime.of(year, 12, 31, 0, 0));
	}

	/**
	 * @return aktuelle Kalenderwoche eines Jahres
	 */
	public static int getWeekOfYear(LocalDateTime dateTime) {
		return dateTime
			.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
	}

	// Chat GPT 3.5
	// Promt: Wie bekomme ich das Start-Datum zu einer bestimmten Woche?

	/**
	 *
	 * @param year das Jahr
	 * @param week die Kalenderwoche zum angegebenen Jahr
	 * @return Startdatum der Woche (Montag um 00:00)
	 */
	public static LocalDateTime getStartWeekDateTime(int year, int week) {
		WeekFields weekFields = WeekFields.of(Locale.getDefault());

		return LocalDateTime.of(year, 1, 1, 0, 0)
			.with(weekFields.weekOfYear(), week)
			.with(weekFields.dayOfWeek(), 1);
	}

	/**
	 * @param dateTime beliebiges Datum (+ Zeitpunk)
	 * siehe {@link #getStartWeekDateTime(int, int)}
	 */
	public static LocalDateTime getStartWeekDateTime(LocalDateTime dateTime) {
		return getStartWeekDateTime(
			dateTime.getYear(),
			getWeekOfYear(dateTime)
		);
	}

	/**
	 *
	 * @param year das Jahr
	 * @param week die Kalenderwoche zum angegebenen Jahr
	 * @return Enddatum der Woche (Sonntag um 23:59)
	 */
	public static LocalDateTime getEndWeekDateTime(int year, int week) {
		WeekFields weekFields = WeekFields.of(Locale.getDefault());

		return LocalDateTime.of(year, 1, 1, 23, 59)
			.with(weekFields.weekOfYear(), week)
			.with(weekFields.dayOfWeek(), 7);
	}

	/**
	 * @param dateTime beliebiges Datum (+ Zeitpunk)
	 * siehe {@link #getEndWeekDateTime(int, int)}
	 */
	public static LocalDateTime getEndWeekDateTime(LocalDateTime dateTime) {
		return getEndWeekDateTime(
			dateTime.getYear(),
			getWeekOfYear(dateTime)
		);
	}

	/**
	 * @return Ausgabe String: "Woche-Von-Datum (dd.MM.yyyy) - Woche-Bis-Datum (dd.MM.yyyy)"
	 */
	public static String getWeekRangeFormat(int year, int week) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());

		String formattedStartDate = getStartWeekDateTime(year, week).format(formatter);
		String formattedEndDate = getEndWeekDateTime(year, week).format(formatter);

		return formattedStartDate + " - " + formattedEndDate;
	}
	/**
	 * @param dateTime beliebiges Datum (+ Zeitpunk)
	 * siehe {@link #getWeekRangeFormat(int, int)}
	 */
	public static String getWeekRangeFormat(LocalDateTime dateTime) {
		return getWeekRangeFormat(dateTime.getYear(), getWeekOfYear(dateTime));
	}

	/**
	 * @return Ausgabe String: "Wochentag, Datum (dd.MM.yyyy)"
	 */
	public static String getDayFormat(LocalDate date) {
		// EEEE: Wochentag, Sprache hängt von Locale.getDefault() ab
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", Locale.getDefault());
		return date.format(formatter);
	}
}
