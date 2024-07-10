package de.ufo.cinemasystem.datainitializer;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import de.ufo.cinemasystem.services.CinemaShowService;

/**
 * DataInitialiser für Kinovorführungen
 * @author Yannick Harnisch
 */
@Component
// Testdaten der Kinoveranstaltungen werden nach den Testdaten für die Filme und der Kinosäle erstellt (deshalb: Order = 3)
@Order(4)
public class CinemaShowDataInitializer implements DataInitializer {

	private final CinemaShowRepository cinemaShowRepository;

	private final CinemaShowService cinemaShowService;

	private final CinemaHallRepository cinemaHallRepository;

	private final FilmRepository filmRepository;

	CinemaShowDataInitializer(CinemaShowRepository cinemaShowRepository, CinemaShowService cinemaShowService,
							  CinemaHallRepository cinemaHallRepository, FilmRepository filmRepository) {
		Assert.notNull(cinemaShowRepository, "CinemaShowRepository must not be null!");
		Assert.notNull(cinemaShowService, "CinemaShowService must not be null!");
		Assert.notNull(cinemaHallRepository, "cinemaHallRepository must not be null!");
		Assert.notNull(filmRepository, "FilmRepository must not be null!");

		this.cinemaShowRepository = cinemaShowRepository;
		this.cinemaShowService = cinemaShowService;
		this.cinemaHallRepository = cinemaHallRepository;
		this.filmRepository = filmRepository;
	}

	@Override
	public void initialize() {
		// verhindert doppelte Kinoveranstaltung-Einträge,
		// falls Testdaten bereits in der Datenbank
		if(cinemaShowRepository.findAll().iterator().hasNext()) {
			return;
		}

		Random random = new Random();
		List<Film> allFilms = filmRepository.findAll().toList();
		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();

		// TestDaten:
		// Speichert 10 Veranstaltungen, im Abstand von 24 Stunden in den nächsten Tagen.
		// Filme werden zufällig aus den aktuellen bestehenden Filmen ausgewählt.
		// Der Basis-Preis ist konstant.
		for(int i = 0; i < 10; i++) {
			Film film = allFilms.get(random.nextInt(allFilms.size()));
			cinemaShowService.createCinemaShow(
				LocalDateTime.now().plusDays(i),
				film.getPrice(),
				film,
				allCinemaHalls.get(random.nextInt(allCinemaHalls.size()))
			);
		}
		//zwei Reservierungstests
		Film film = allFilms.get(random.nextInt(allFilms.size()));
		cinemaShowService.createCinemaShow(
			LocalDateTime.now().plusMinutes(60),
			film.getPrice(),
			film,
			allCinemaHalls.get(random.nextInt(allCinemaHalls.size())));
		film = allFilms.get(random.nextInt(allFilms.size()));
		cinemaShowService.createCinemaShow(
			LocalDateTime.now().plusMinutes(50),
			film.getPrice(),
			film,
			allCinemaHalls.get(random.nextInt(allCinemaHalls.size())));


		// Gebe alle Veranstaltungen aus, welche aktuell in der Datenbank liegen:
		System.out.println("films: " + allFilms.size());
		cinemaShowRepository.findAll().forEach(cs -> {
			System.out.println("ID: " + cs.getId());
			System.out.println("Film: " + cs.getFilm().toString());
			System.out.println("Start: " + cs.getStartDateTime().toString());
			System.out.println("Preis: " + cs.getBasePrice().toString());
			System.out.println("Kinosaal: " + cs.getCinemaHall().getName());
			System.out.println("=======================================");
		});
	}
}
