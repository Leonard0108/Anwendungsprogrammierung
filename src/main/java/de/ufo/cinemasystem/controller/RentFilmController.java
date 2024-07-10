package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.YearWeekEntry;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Spring MVC-Controller des Filmverleihs
 * @author Yannick Harnisch
 */
@Controller
public class RentFilmController {

	private FilmRepository filmRepository;
	private CinemaShowRepository cinemaShowRepository;

        /**
         * Erstelle einen neuen Controller mit den angegebenen Abhängigkeiten.
         * @param filmRepository Implementierung Film-Repository
         * @param cinemaShowRepository Implementierung CinemaShow-Repository
         */
	public RentFilmController(FilmRepository filmRepository, CinemaShowRepository cinemaShowRepository) {
		this.filmRepository = filmRepository;
		this.cinemaShowRepository = cinemaShowRepository;
	}

        /**
         * GET-Endpunkt: Verleih-Status anzeigen
         * @param model Modell
         * @return "films-rental"
         */
	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@GetMapping("/rent-films")
	public String getRentFilms(Model model) {
		model.addAttribute("allFilms", filmRepository.findAll());
                model.addAttribute("title", "Filme ausleihen");
                
		return "films-rental";
	}

        /**
         * GET-Endpunkt: Detailansicht eines Films
         * @param model Modell
         * @param film anzuzeigender Film
         * @return "film-detail"
         */
	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@GetMapping("/films/{film}")
	public String getRentFilm(Model model, @PathVariable Film film) {
		List<YearWeekEntry> allRangeYearWeeks = new ArrayList<>();

		// Wochen welche geliehen wurden
		// value[0]: Leihkosten, welche fest vorgegeben sind und sich für fortschreitende Wochen reduzieren können
		// TODO: implementieren, Ansatz überlegen (hoher Berechnungsaufwand,
		//  für jede Woche muss für jede dort stattfindende Veranstaltung alle gekauften Tickets (auch noch abhängig von der Platzgruppe und der Ermäßigung) summiert werden)
		// value[1]: 30 % der Ticket-Einnahmen durch Vorführungen zu dem Film (Datenquelle: alle gefilterten Bestellungen zum Film)
		HashMap<YearWeekEntry, Integer[]> reservedWeeks = new HashMap<>();
		// Wochen, welche in der Vergangenheit liegen und die aktuelle Woche, diese sind geblockt
		// (können also nicht mehr storniert oder geliehen werden)
		HashSet<YearWeekEntry> nowAndPastWeeks = new HashSet<>();
		// Wochen welche geliehen wurden, aber aktuell nicht wieder storniert werden können,
		// da Vorführungen zu dem Film in der Woche existieren
		HashSet<YearWeekEntry> blockedReservedWeeks = new HashSet<>();

		addDetailNowAndPastWeeks(film, allRangeYearWeeks, reservedWeeks, nowAndPastWeeks);
		addDetailFutureWeeks(film, allRangeYearWeeks, reservedWeeks, blockedReservedWeeks);

		int sumRentFee = 0;
		for(Integer[] weeklyRentFee : reservedWeeks.values()) {
			sumRentFee += (weeklyRentFee[0] + weeklyRentFee[1]);
		}

		int[] totalSeatOccupancyValues = getTotalSeatOccupancyValues(film);

		model.addAttribute("film", film);
		model.addAttribute("allRangeYearWeeks", allRangeYearWeeks);
		model.addAttribute("reservedWeeks", reservedWeeks);
		model.addAttribute("nowAndPastWeeks", nowAndPastWeeks);
		model.addAttribute("blockedReservedWeeks", blockedReservedWeeks);

		model.addAttribute("freeSeatCount", totalSeatOccupancyValues[0]);
		model.addAttribute("reservedSeatCount", totalSeatOccupancyValues[1]);
		model.addAttribute("boughtSeatCount", totalSeatOccupancyValues[2]);
		model.addAttribute("sumRentFee", sumRentFee);
                model.addAttribute("title", film.getTitle());

		return "film-detail";
	}

        /**
         * POST-Endpunkt: Verleihstatus ändern
         * @param redirectAttributes Redirect-Modell
         * @param film zu ändernder Film
         * @param newSelectedRentWeeks auszuleihende Wochen (Checkbox-Verhalten)
         * @param newDisabledCheckedRentWeeks deaktivierte Wochen
         * @return "redirect:/films/{film}"
         */
	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/films/{film}")
	public String postRentFilm(RedirectAttributes redirectAttributes, @PathVariable Film film,
							   @RequestParam(name = "new-selected-rent-weeks", required = false) List<Long> newSelectedRentWeeks,
							   @RequestParam(name = "new-disabled-checked-rent-weeks", required = false) List<Long> newDisabledCheckedRentWeeks) {
		Set<Long> allCheckedWeeks = new HashSet<>();
		if(newSelectedRentWeeks == null)
			newSelectedRentWeeks = new ArrayList<>();

		if(newDisabledCheckedRentWeeks == null)
			newDisabledCheckedRentWeeks = new ArrayList<>();

		YearWeekEntry currentWeek = getDetailStartYearWeekEntry(film);
		YearWeekEntry lastWeek = getDetailEndYearWeekEntry();

		while(currentWeek.compareTo(lastWeek) <= 0) {
			if(!newDisabledCheckedRentWeeks.contains(currentWeek.getId())) {
				if(newSelectedRentWeeks.contains(currentWeek.getId()))
					film.addRentWeek(currentWeek);
				else
					film.removeRentWeek(currentWeek);
			}

			currentWeek = currentWeek.nextWeek();
		}

		/*
		// Fasst alle ungleichen Einträge auf,
		// wandelt diese in YearWeekEntry's um und speichert zusätzlich den Zielwert (false: removeRentWeek, true: addRentWeek)
		// zu jedem Eintrag.
		// (alte Woche = 1 && neue Woche = 0)
		// TODO: effizienter umsetzen
		Map<YearWeekEntry, Boolean> unequalRentWeeks = film.getRentWeeks()
			.stream()
			.map(YearWeekEntry::getId)
			.filter(Predicate.not(newSelectedRentWeeks::contains))
			.collect(Collectors.toMap(YearWeekEntry::new, e -> false));
		// || (alte Woche = 0 && neue Woche = 1)
		unequalRentWeeks.putAll(newSelectedRentWeeks
			.stream()
			.map(YearWeekEntry::new)
			.filter(Predicate.not(film.getRentWeeks().toList()::contains))
			.collect(Collectors.toMap(e -> e, e -> true))
		);

		// ändert die Filmdaten
		unequalRentWeeks.forEach((key, value) -> {
			System.out.println(key.getWeekRangeFormat() + ", " + value);
			if(value) film.addRentWeek(key);
			else film.removeRentWeek(key);
		});
		 */

		filmRepository.save(film);
		redirectAttributes.addFlashAttribute("successMessage", "Leihzeitraum dieses Films geändert!");

		return "redirect:/films/{film}";
	}

	/**
	 * Fügt alle Wochen ausgehend von der ersten Leihwoche
	 * (wenn vorhanden und in Vergangenheit) bis zur aktuellen Woche in allRangeYearWeeks hinzu (Zeitraum).
	 * @param film der aktuelle Film, für die Detailansicht
	 * @param allRangeYearWeeks wird modifiziert
	 * @param reservedWeeks wird modifiziert, alle reservierten Wocheneinträge aus dem Zeitraum werden hier hinzugefügt
	 * @param nowAndPastWeeks wird modifiziert, alle Einträge aus dem Zeitraum werden hinzugefügt,
	 *                        da diese in der Vergangenheit liegen oder in der aktuellen Woche
	 */
	private void addDetailNowAndPastWeeks(Film film, List<YearWeekEntry> allRangeYearWeeks,
										  HashMap<YearWeekEntry, Integer[]> reservedWeeks,
										  HashSet<YearWeekEntry> nowAndPastWeeks) {
		YearWeekEntry nextWeek = YearWeekEntry.getNowYearWeek().nextWeek();
		YearWeekEntry currentWeek = getDetailStartYearWeekEntry(film);

		// füge alle Wochen von der firstWeek bis zur aktuellen Woche der Liste hinzu
		while(currentWeek.compareTo(nextWeek) < 0) {
			allRangeYearWeeks.add(currentWeek);
			nowAndPastWeeks.add(currentWeek);
			if(film.isRent(currentWeek)) {
				// Value[0]: Aktuelle Woche, welche reserviert wird,
				// wird indirekt wieder gespiegelt durch reservedWeeks.size() + 1,
				// da reservedWeeks Einträge von der ersten Woche an richtig sortiert hinzugefügt werden
				reservedWeeks.put(
					currentWeek,
					new Integer[]{
						film.getBasicRentFee(reservedWeeks.size() + 1),
						0
					}
				);
			}

			currentWeek = currentWeek.nextWeek();
		}
	}

	/**
	 * gibt erste Woche, welche in der Tabelle angezeigt werden soll:
	 * Fall 1: Es gibt noch keine Leihwochen bzw. die Leihwochen liegen in der Zukunft, dann soll ab der nächsten Woche alles angezeigt werden
	 * Fall 2: einige Leihwochen liegen bereits in der Vergangenheit bzw. in der aktuellen Woche, so soll die erste Leihwoche des Films ausgewählt werden
	 */
	private YearWeekEntry getDetailStartYearWeekEntry(Film film) {
		Optional<YearWeekEntry> firstFilmRentWeek = film.getFirstRentWeek();
		YearWeekEntry nextWeek = YearWeekEntry.getNowYearWeek().nextWeek();

        return (firstFilmRentWeek.isEmpty() || nextWeek.compareTo(firstFilmRentWeek.get()) <= 0) ?
			nextWeek : firstFilmRentWeek.get();
	}

	// TODO: effizienter umsetzen
	/**
	 * gibt letzte Woche (8. Woche in Zukunft) zurück, welche in der Tabelle angezeigt werden soll:
	 */
	private YearWeekEntry getDetailEndYearWeekEntry() {
		final int futureRentWeeks = 8;
		YearWeekEntry currentWeek = YearWeekEntry.getNowYearWeek().nextWeek();

		for(int i = 0; i < futureRentWeeks - 1; i++) {
			currentWeek = currentWeek.nextWeek();
		}

		return currentWeek;
	}

	/**
	 * Fügt alle Wochen ausgehend von der nächsten Leihwoche (in Bezug auf die aktuelle Woche)
	 * bis zur 8. Wochen in Zukunft hinzu (Zeitraum).
	 * @param film der aktuelle Film, für die Detailansicht
	 * @param allRangeYearWeeks wird modifiziert
	 * @param reservedWeeks wird modifiziert, alle reservierten Wocheneinträge aus dem Zeitraum werden hier hinzugefügt
	 * @param blockedReservedWeeks wird modifiziert, alle reservierten Wocheneinträge, wobei zusätzlich mind. eine Veranstaltung
	 *                             in der Woche für den Film vorhanden sein muss, werden hier hinzugefügt.
	 *                             (d.h. dies sind Wochen-Einträge, welche zu dem Zeitpunkt nicht storniert werden können,
	 *                             da dort noch aktive Vorführungen vorliegen und diese erst weggelegt oder gelöscht werden müssen)
	 */
	private void addDetailFutureWeeks(Film film, List<YearWeekEntry> allRangeYearWeeks,
									  HashMap<YearWeekEntry, Integer[]> reservedWeeks,
									  HashSet<YearWeekEntry> blockedReservedWeeks) {
		final int futureRentWeeks = 8;
		YearWeekEntry currentWeek = YearWeekEntry.getNowYearWeek().nextWeek();

		// füge futureRentWeeks der Liste hinzu, somit sind Filme aktuell 8 Wochen im voraus ausleihbar
		for(int i = 0; i < futureRentWeeks; i++) {
			allRangeYearWeeks.add(currentWeek);
			if(!cinemaShowRepository.findCinemaShowsInWeek(currentWeek.getYear(), currentWeek.getWeek(), film).isEmpty())
				blockedReservedWeeks.add(currentWeek);
			if(film.isRent(currentWeek)) {
				// Value[0]: Aktuelle Woche, welche reserviert wird,
				// wird indirekt wieder gespiegelt durch reservedWeeks.size(),
				// da reservedWeeks Einträge von der ersten Woche an richtig sortiert hinzugefügt werden
				reservedWeeks.put(
					currentWeek,
					new Integer[]{
						film.getBasicRentFee(reservedWeeks.size() + 1),
						0
					}
				);
			}

			currentWeek = currentWeek.nextWeek();
		}
	}

	/**
	 * Gibt alle Sitzplatz-Belegungs-Statistiks-Werte zurück von allen Veranstaltungen für den Film
	 * @param film Statistiken für diesen Film
	 * @return [0]: freePlaces, [1]: reservedPlaces, [2]: boughtPlaces
	 */
	private int[] getTotalSeatOccupancyValues(Film film) {
		var cinemaShows = this.cinemaShowRepository.findAllByFilm(film);
		int freePlaces = 0, reservedPlaces = 0, boughtPlaces = 0;

		for(CinemaShow cinemaShow : cinemaShows) {
			freePlaces += cinemaShow.getSeatCount(Seat.SeatOccupancy.FREE);
			reservedPlaces += cinemaShow.getSeatCount(Seat.SeatOccupancy.RESERVED);
			boughtPlaces += cinemaShow.getSeatCount(Seat.SeatOccupancy.BOUGHT);
		}

		return new int[]{freePlaces, reservedPlaces, boughtPlaces};
	}
}
