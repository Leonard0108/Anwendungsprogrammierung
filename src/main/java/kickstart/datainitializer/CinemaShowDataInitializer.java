package kickstart.datainitializer;

import kickstart.models.CinemaShow;
import kickstart.models.Film;
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
// Testdaten der Kinoveranstaltungen werden nach den Testdaten für die Filme erstellt (deshalb: Order = 2)
@Order(2)
public class CinemaShowDataInitializer implements DataInitializer {

	private final CinemaShowRepository cinemaShowRepository;

	private final FilmRepository filmRepository;

	CinemaShowDataInitializer(CinemaShowRepository cinemaShowRepository, FilmRepository filmRepository) {
		Assert.notNull(cinemaShowRepository, "CinemaShowRepository must not be null!");
		Assert.notNull(filmRepository, "FilmRepository must not be null!");

		this.cinemaShowRepository = cinemaShowRepository;
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
		List<Film> allFilms = filmRepository.findAll().toList();

		// TestDaten:
		// Speichert 10 Veranstaltungen, im Abstand von 24 Stunden in den nächsten Tagen.
		// Filme werden zufällig aus den aktuellen bestehenden Filmen ausgewählt.
		// Der Basis-Preis ist konstant.
		for(int i = 0; i < 10; i++) {
			cinemaShowRepository.save(new CinemaShow(
				LocalDateTime.now().plusDays(i),
				Money.of(10.99, EURO),
				allFilms.get(random.nextInt(allFilms.size()))
			));
		}

		// Gebe alle Veranstaltungen aus, welche aktuell in der Datenbank liegen:
		System.out.println("films: " + allFilms.size());
		cinemaShowRepository.findAll().forEach(cs -> {
			System.out.println("ID: " + cs.getId());
			System.out.println("Film: " + cs.getFilm().toString());
			System.out.println("Start: " + cs.getStartDateTime().toString());
			System.out.println("Preis: " + cs.getBasePrice().toString());
			System.out.println("=======================================");
		});
	}
}
