package de.ufo.cinemasystem.datainitializer;

import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.models.CinemaHallService;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.ufo.cinemasystem.repository.CinemaHallRepository;

import java.util.AbstractMap;

@Component
@Order(3)
public class CinemaHallDataInitializer implements DataInitializer {

	private final CinemaHallRepository cinemaHallRepository;
	private final CinemaHallService cinemaHallService;

	public CinemaHallDataInitializer(CinemaHallService cinemaHallService, CinemaHallRepository cinemaHallRepository) {
		Assert.notNull(cinemaHallService, "cinemaHallService must not be null!");
		Assert.notNull(cinemaHallRepository, "cinemaHallRepository must not be null!");

		this.cinemaHallService = cinemaHallService;
		this.cinemaHallRepository = cinemaHallRepository;
	}

	@Override
	public void initialize() {
		// verhindert doppelte Kinosaal-Einträge,
		// falls Testdaten bereits in der Datenbank
		if(cinemaHallRepository.findAll().iterator().hasNext()) {
			return;
		}

		// Erstelle 6 Kinosäle:
		for(int i = 0; i < 6; i++) {
			cinemaHallService.createCinemaHall("Saal " + i,
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_1),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_1),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_1),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_1),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_2),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_2),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_2),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_2),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_3),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_3),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_3),
				new AbstractMap.SimpleEntry<>(18, Seat.PlaceGroup.GROUP_3)
				);
		}

		System.out.println("Kinosäle:");
		cinemaHallRepository.findAll().forEach(ch -> {
			System.out.println(ch.getName());
		});
	}
}
