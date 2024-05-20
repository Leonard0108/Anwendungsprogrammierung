package de.ufo.cinemasystem.models;

import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CinemaShowService {

	@Autowired
	private CinemaHallRepository cinemaHallRepository;

	@Autowired
	private CinemaShowRepository cinemaShowRepository;

	public CinemaShow createCinemaShow(LocalDateTime startDateTime, Money basePrice, Film film, CinemaHall cinemaHall) {
		CinemaShow cinemaShow = new CinemaShow(
			startDateTime,
			basePrice,
			film
		);
		final Map<Seat, Seat.SeatOccupancy> seats = new TreeMap<>();
		for(Seat seat : cinemaHall.getSeats()) {
			seats.put(seat, Seat.SeatOccupancy.FREE);
		}
		cinemaShow.initSeats(seats);

		cinemaHall.addCinemaShow(cinemaShow);

		// Kinosaal und Vorführung müssen gespeichert werden,
		// damit die bidirektionale Beziehung hergestellt werden kann.
		// Hinweis: zuerst den Kinosaal speichern!
		cinemaHallRepository.save(cinemaHall);
		cinemaShowRepository.save(cinemaShow);

		return cinemaShow;
	}
}
