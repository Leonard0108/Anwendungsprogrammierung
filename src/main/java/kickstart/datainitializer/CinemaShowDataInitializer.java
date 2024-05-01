package kickstart.datainitializer;

import ch.qos.logback.core.net.SyslogOutputStream;
import kickstart.models.CinemaHall;
import kickstart.models.CinemaShow;
import kickstart.models.Film;
import kickstart.repository.CinemaHallRepository;
import kickstart.repository.CinemaShowRepository;
import kickstart.repository.FilmRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.salespointframework.core.Currencies.EURO;

@Component
// Testdaten der Kinoveranstaltungen werden nach den Testdaten für die Filme und der Kinosäle erstellt (deshalb: Order = 3)
@Order(3)
public class CinemaShowDataInitializer implements DataInitializer {

	private final CinemaShowRepository cinemaShowRepository;

	private final CinemaHallRepository cinemaHallRepository;

	private final FilmRepository filmRepository;

	CinemaShowDataInitializer(CinemaShowRepository cinemaShowRepository, CinemaHallRepository cinemaHallRepository, FilmRepository filmRepository) {
		Assert.notNull(cinemaShowRepository, "CinemaShowRepository must not be null!");
		Assert.notNull(cinemaHallRepository, "cinemaHallRepository must not be null!");
		Assert.notNull(filmRepository, "FilmRepository must not be null!");

		this.cinemaShowRepository = cinemaShowRepository;
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
		CinemaShow show;
		CinemaHall hall;
		List<Film> allFilms = filmRepository.findAll().toList();
		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();

		// TestDaten:
		// Speichert 10 Veranstaltungen, im Abstand von 24 Stunden in den nächsten Tagen.
		// Filme werden zufällig aus den aktuellen bestehenden Filmen ausgewählt.
		// Der Basis-Preis ist konstant.
		for(int i = 0; i < 10; i++) {
			show = new CinemaShow(
					LocalDateTime.now().plusDays(i),
					Money.of(10.99, EURO),
					allFilms.get(random.nextInt(allFilms.size()))
			);
			hall = allCinemaHalls.get(random.nextInt(allCinemaHalls.size()));

			hall.addCinemaShow(show);

			// Kinosaal und Vorführung müssen gespeichert werden,
			// damit die bidirektionale Beziehung hergestellt werden kann.
			// Hinweis: zuerst den Kinosaal speichern!
			cinemaHallRepository.save(hall);
			cinemaShowRepository.save(show);
		}

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
