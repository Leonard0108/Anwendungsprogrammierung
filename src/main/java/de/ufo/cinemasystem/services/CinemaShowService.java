package de.ufo.cinemasystem.services;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CinemaShowService {

	@Autowired
	private CinemaHallRepository cinemaHallRepository;

	@Autowired
	private CinemaShowRepository cinemaShowRepository;


	/**
	 * Erstellt eine neue Kino-Vorführung (CinemaShow-Objekt) und speichert dieses in der Datenbank.
	 * @param startDateTime Startzeit der Vorführung muss mind. 1/2 Stunde in Zukunft liegen
	 *                      und der Kinosaal ist für die Zeitspanne von Beginn bis Ende frei
	 *                      (keine anderen Vorführungen und Events)
	 * @param basePrice Basis-Ticket-Preis der Veranstaltung
	 * @param film Film, welcher laufen soll
	 * @param cinemaHall Kinosaal, in welchem die Vorführung laufen soll. (Bedingung: siehe startDateTime)
	 * @return neue Kino-Vorführung
	 */
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

		// Stelle Bidirektional Verbindung zwischen Kinosaal und Vorführung her
		cinemaHall.addCinemaShow(cinemaShow);

		// Kinosaal und Vorführung müssen gespeichert werden,
		// damit die bidirektionale Beziehung hergestellt werden kann.
		// Hinweis: zuerst den Kinosaal speichern!
		cinemaHallRepository.save(cinemaHall);
		cinemaShowRepository.save(cinemaShow);

		return cinemaShow;
	}

	/**
	 * löscht CinemaShow (auch aus Datenbank) und entfernt auch alle Beziehungen zu anderen @Entity-Objekten
	 * @param cinemaShow Kino-Vorführung, welche gelöscht werden soll
	 * @return true, wenn Kino-Vorführung erfolgreich gelöscht wurde, sonst false
	 */
	public boolean deleteCinemaShow(CinemaShow cinemaShow) {
		// TODO: prüfe ob Vorführung in der Vergangenheit liegt

		CinemaHall cinemaHall = cinemaShow.getCinemaHall();
		cinemaHall.removeCinemaShow(cinemaShow);

		cinemaHallRepository.save(cinemaHall);
		cinemaShowRepository.delete(cinemaShow);

		return true;
	}

	/**
	 * Builder, um vorhandene CinemaShow-Objekte effizient zu aktualisieren (das Objekt und in der Datenbank)
	 */
	public static class CinemaShowUpdater {
		private CinemaShow originalCinemaShow;
		private CinemaShowService service;

		private LocalDateTime startDateTime;
		private Money basePrice;
		private Film film;
		private final Map<Seat, Seat.SeatOccupancy> seats = new HashMap<>();

		// nicht änderbar, verursacht Probleme
		//private CinemaHall cinemaHall;

		private CinemaShowUpdater(CinemaShow cinemaShow, CinemaShowService service) {
			this.originalCinemaShow = cinemaShow;
			this.service = service;
		}

		/**
		 * Setzt neues Startdatum- und Zeit im CinemaShowUpdater-Objekt.
		 * Diese Methode verändert noch keine Daten am CinemaShow-@Entity-Objekt oder in der Datenbank.
		 * @param startDateTime neues Startdatum- und Zeit, welcher für die Vorführung gilt,
		 *                      Startzeit muss mind. 1/2 Stunde in der Zukunft liegen.
		 * @return Builder zum Setzen weiterer Daten oder zum finalen Speichern ({@link  #save()}) der Daten
		 */
		public CinemaShowUpdater setStartDateTime(LocalDateTime startDateTime) {
			// TODO: prüfe ob in Vergangenheit
			this.startDateTime = startDateTime;
			return this;
		}

		/**
		 * Setzt den neuen Basis-Ticket-Preis im CinemaShowUpdater-Objekt.
		 * Diese Methode verändert noch keine Daten am CinemaShow-@Entity-Objekt oder in der Datenbank.
		 * @param basePrice neuer Basis-Ticket-Preis, welcher für die Vorführung gilt,
		 *                  Währung muss Euro und größer 0,00 sein.
		 * @return Builder zum Setzen weiterer Daten oder zum finalen Speichern ({@link  #save()}) der Daten
		 */
		public CinemaShowUpdater setBasePrice(Money basePrice) {
			// TODO: prüfe größer 0, Währung = Euro
			this.basePrice = basePrice;
			return this;
		}

		/**
		 * Setzt den neuen Film im CinemaShowUpdater-Objekt.
		 * Diese Methode verändert noch keine Daten am CinemaShow-@Entity-Objekt oder in der Datenbank.
		 * @param film neuer Film, welcher bei der Vorführung laufen soll
		 * @return Builder zum Setzen weiterer Daten oder zum finalen Speichern ({@link  #save()}) der Daten
		 */
		public CinemaShowUpdater setFilm(Film film) {
			this.film = film;
			return this;
		}

		/**
		 * Ändert die Sitzplatz-Belegung eines Sitzplatzes im CinemaShowUpdater-Objekt.
		 * Diese Methode verändert noch keine Daten am CinemaShow-@Entity-Objekt oder in der Datenbank.
		 * @param seat Sitzplatz, mit neuer Belegung
		 * @param occupancy Sitzplatz-Belegung
		 * @return Builder zum Setzen weiterer Daten oder zum finalen Speichern ({@link  #save()}) der Daten
		 */
		public CinemaShowUpdater setSeatOccupancy(Seat seat, Seat.SeatOccupancy occupancy) {
			this.seats.put(seat, occupancy);
			return this;
		}

		/**
		 * Aktualisiert die gespeicherten CinemaShow-Daten im richtigen CinemaShow-Objekt und in der Datenbank.
		 * Der CinemaShowUpdater-Builder sollte nach dem Aufruf dieser Methode nicht mehr verwendet werden.
		 * @return aktualisiertes CinemaShow-Objekt
		 */
		public CinemaShow save() {
			return this.service.saveEditCinemaShow(this);
		}
	}

	/**
	 * Erstellt einen neuen Builder mit welchen Daten gesetzt werden und anschließend
	 * die Datenbank und das CinmaShow-Objekt aktualisiert wírd.
	 * Voraussetzung: Die Kino-Vorführung liegt mind 1/2 Stunde in der Zukunft
	 * @param cinemaShow die zu verändernde Kino-Vorführung
	 * @throws RuntimeException, wenn die CinemaShow nicht im cinemaShowRepository existiert
	 * @return Builder zum Setzen von Daten.
	 */
	public CinemaShowUpdater update(CinemaShow cinemaShow) {
		// TODO check ob Vorführung mind 1/2 Stunde in der Zukunft liegt,
		//  bereits vergangene Vorführungen sollen nicht mehr geändert werden können.
		return new CinemaShowUpdater(cinemaShow, this);
	}

	/**
	 * Erstellt einen neuen Builder mit welchen Daten gesetzt werden und anschließend
	 * die Datenbank und das CinmaShow-Objekt aktualisiert wírd.
	 * Voraussetzung: Die Kino-Vorführung liegt mind 1/2 Stunde in der Zukunft
	 * @param id Referenz zur Kino-Vorführung
	 * @throws RuntimeException, wenn die CinemaShow nicht im cinemaShowRepository existiert
	 * @return Builder zum Setzen von Daten.
	 */
	public CinemaShowUpdater update(Long id) {
		// TODO check ob Vorführung mind 1/2 Stunde in der Zukunft liegt,
		//  bereits vergangene Vorführungen sollen nicht mehr geändert werden können.
		return new CinemaShowUpdater(
			cinemaShowRepository.findById(id).orElseThrow(() -> new RuntimeException("CinemaShow nicht gefunden!")),
			this
		);
	}

	/**
	 * Verändert ein vorhandenes CinemaShow-Objekt (vorhanden im CinemaShowUpdater Objekt) durch die im CinemaShowUpdater gesetzten Daten
	 * und speichert dieses in der Datenbank ab.
	 * Es ist nicht empfehlenswert diese Methode manuell aufzurufen, sondern dies CinemaShowUpdater#save() zu überlassen!
	 * @param updater Builder, welcher aktualisierte CinemaShow-Daten enthält.
	 * @return aktualisierte CinemaShow Objekt
	 */
	@Transactional
	public CinemaShow saveEditCinemaShow(CinemaShowUpdater updater) {
		// TODO prüfe ob startDateTime in Vergangenheit liegt

		CinemaShow cinemaShow = updater.originalCinemaShow;
		if(updater.startDateTime != null)
			cinemaShow.setStartDateTime(updater.startDateTime);
		if(updater.basePrice != null)
			cinemaShow.setBasePrice(updater.basePrice);
		if(updater.film != null)
			cinemaShow.setFilm(updater.film);
		for(var entry : updater.seats.entrySet()) {
			cinemaShow.setOccupancy(entry.getKey(), entry.getValue());
		}
		// theoretisch cinemaHall nicht noch mal speichern, da keine Änderungen an der CinemaHall
		return cinemaShowRepository.save(cinemaShow);
	}
}
