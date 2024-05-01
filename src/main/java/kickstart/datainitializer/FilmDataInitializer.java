package kickstart.datainitializer;

import ch.qos.logback.core.net.SyslogOutputStream;
import kickstart.models.CinemaShow;
import kickstart.models.Film;
import kickstart.repository.CinemaShowRepository;
import kickstart.repository.FilmRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.salespointframework.core.Currencies.EURO;

@Component
// Testdaten für die Filme werden als erstes erzeugt (Order = 1)
@Order(1)
public class FilmDataInitializer implements DataInitializer {

	private final FilmRepository filmRepository;

	FilmDataInitializer(FilmRepository filmRepository) {
		Assert.notNull(filmRepository, "FilmRepository must not be null!");

		this.filmRepository = filmRepository;
	}

	@Override
	public void initialize() {
		// verhindert doppelte Film-Einträge,
		// falls Testdaten bereits in der Datenbank
		if(filmRepository.findAll().iterator().hasNext()) {
			return;
		}

		Random random = new Random();
		List<Integer> fsks = List.of(0,6,12,16,18);
		CinemaShow show;
		List<Film> allFilms = filmRepository.findAll().toList();

		// TestDaten:
		// Speichert 10 Filme mit ansteigender Filmlänge und zufälliger fsk.
		for(int i = 0; i < 10; i++) {
			filmRepository.save(new Film(
				"Film " + i,
				"Film-Beschreibung " + i,
				90 + i * 10,
				fsks.get(random.nextInt(fsks.size()))
			));
		}

		// Gebe alle Filme aus, welche aktuell in der Datenbank liegen:
		filmRepository.findAll().forEach(f -> {
			System.out.println(f.toString());
			System.out.println("Beschreibung: " + f.getDesc());
			System.out.println("FSK: " + f.getFskAge());
			System.out.println("ID: " + f.getId());
			System.out.println("=======================================");
		});
	}
}
