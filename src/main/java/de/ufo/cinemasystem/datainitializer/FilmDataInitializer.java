package de.ufo.cinemasystem.datainitializer;

import ch.qos.logback.core.net.SyslogOutputStream;
import de.ufo.cinemasystem.additionalfiles.YearWeekEntry;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.FilmProvider;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmProviderRepository;
import de.ufo.cinemasystem.repository.FilmRepository;

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
@Order(2)
public class FilmDataInitializer implements DataInitializer {

	private final FilmRepository filmRepository;

	private final FilmProviderRepository filmProviderRepository;

	FilmDataInitializer(FilmRepository filmRepository, FilmProviderRepository filmProviderRepository) {
		Assert.notNull(filmRepository, "FilmRepository must not be null!");
		Assert.notNull(filmProviderRepository, "FilmProviderRepository must not be null!");

		this.filmRepository = filmRepository;
		this.filmProviderRepository = filmProviderRepository;
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
		List<FilmProvider> allFilmProviders = filmProviderRepository.findAll().toList();

		// TestDaten:
		// Speichert 10 Filme mit ansteigender Filmlänge und zufälliger fsk.
		for(int i = 0; i < 10; i++) {
			Film film = new Film(
				"Film " + i,
				"Film-Beschreibung " + i,
				90 + i * 10,
				fsks.get(random.nextInt(fsks.size())),
				allFilmProviders.get(random.nextInt(allFilmProviders.size())),
				random.nextInt(1000, 2000)
			);
			film.addRentWeek(new YearWeekEntry(2024, 20));
			film.addRentWeek(new YearWeekEntry(2024, 21));
			film.addRentWeek(new YearWeekEntry(2024, 22));
			film.addRentWeek(new YearWeekEntry(2024, 23));
			film.addRentWeek(new YearWeekEntry(2024, 24));
			film.addRentWeek(new YearWeekEntry(2024, 25));
			if(i <= 5) {
				film.addRentWeek(new YearWeekEntry(2024, 27));
				film.addRentWeek(new YearWeekEntry(2024, 28));
				film.addRentWeek(new YearWeekEntry(2024, 29));
				film.setReducedBasicRentFee(1.0, 1.0, 1.0, 1.0, 1.0, 0.9, 0.8, 0.7, 0.6, 0.5);
			}else {
				film.setReducedBasicRentFee(1.0, 1.0, 1.0, 1.0, 0.8, 0.75, 0.75, 0.6);
			}


			filmRepository.save(film);
		}

		// Gebe alle Filme aus, welche aktuell in der Datenbank liegen:
		filmRepository.findAll().forEach(f -> {
			System.out.println(f.toString());
			System.out.println("Beschreibung: " + f.getDesc());
			System.out.println("FSK: " + f.getFskAge());
			System.out.println("ID: " + f.getId());
			System.out.println("Anbieter: " + f.getFilmProvider().getName());
			System.out.println("Leih-Grundgebür: " + f.getBasicRentFee());
			System.out.println("=======================================");
		});
	}
}
