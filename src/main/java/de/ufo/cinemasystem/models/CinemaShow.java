package de.ufo.cinemasystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.Authenticator;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "CINEMA_SHOWS")
public class CinemaShow implements Comparable<CinemaShow>{
	@Id
	@GeneratedValue
	private Long id;

	private LocalDateTime startDateTime;
	private Money basePrice;

	/* Chat GPT 3.5
   	Promt: Wie fÃ¼ge ich andere Entity Klassen in eine Entity Klasse als Attribute ein?
 	*/
	// Jede Veranstaltung-Instanz verweist auf eine bestimmte Film-Instanz
	@ManyToOne
	//@JoinColumn(name = "film_id")
	private Film film;

	@ElementCollection
	private final Map<Seat, Seat.SeatOccupancy> seats = new TreeMap<>();

	@ManyToOne
	@JoinColumn(name = "cinema_hall_id")
	private CinemaHall cinemaHall;

	CinemaShow(LocalDateTime startDateTime, Money basePrice, Film film) {
		this.startDateTime = startDateTime;
		this.basePrice = basePrice;
		this.film = film;
	}

	public CinemaShow() {}

	public Long getId() {
		return this.id;
	}

	public Film getFilm() {
		return film;
	}

	void setFilm(Film film) {
		this.film = film;
	}

	public LocalDateTime getStartDateTime() {
		return this.startDateTime;
	}

	void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Money getBasePrice() {
		return this.basePrice;
	}

	void setBasePrice(Money basePrice) {
		this.basePrice = basePrice;
	}

	public CinemaHall getCinemaHall() {
		return this.cinemaHall;
	}

	/**
	 * @return alle Sitzplätze und die dazugehörige Sitzplatz-Belegung des Kinosaals
	 */
	public Streamable<Map.Entry<Seat, Seat.SeatOccupancy>> getSeatsAndOccupancy() {
		return Streamable.of(this.seats.entrySet());
	}

	/**
	 * @param row Reihe des Platzes beginnend bei index 0
	 * @param pos Position des Platzes in jeder Reihe beginnend bei index 0, max. 99
	 * @return Sitz und Sitzplatz-Belegung, wenn der Platz an der Stelle im Kinosaal vorhanden ist, sonst empty
	 */
	public Optional<Map.Entry<Seat, Seat.SeatOccupancy>> getSeatAndOccupancy(int row, int pos) {
		Optional<Seat> optSeat = getSeat(row, pos);
		if(optSeat.isEmpty()) return Optional.empty();
		Seat seat = optSeat.get();

		return Optional.of(new AbstractMap.SimpleEntry<>(seat, seats.get(seat)));
	}

	/**
	 * @return alle Sitzplätze des Kinosaals
	 */
	public Streamable<Seat> getSeats() {
		return Streamable.of(this.seats.keySet());
	}

	/**
	 * @param row Reihe des Platzes beginnend bei index 0
	 * @param pos Position des Platzes in jeder Reihe beginnend bei index 0, max. 99
	 * @return Sitz, wenn der Platz an der Stelle im Kinosaal vorhanden ist, sonst empty
	 */
	public Optional<Seat> getSeat(int row, int pos) {
		return this.seats.keySet().stream().filter(s -> s.getRow() == row && s.getPosition() == pos).findAny();
	}

	/**
	 * @param row Reihe des Platzes beginnend bei index 0
	 * @param pos Position des Platzes in jeder Reihe beginnend bei index 0, max. 99
	 * @return Sitzplatz-Belegung, wenn der Platz an der Stelle im Kinosaal vorhanden ist, sonst empty
	 */
	public Optional<Seat.SeatOccupancy> getOccupancy(int row, int pos) {
		return getSeat(row, pos).map(seats::get);
	}

	/**
	 * @param seat Sitzplatz an einer Stelle
	 * @return Sitzplatz-Belegung, wenn der Platz an der Stelle im Kinosaal vorhanden ist, sonst empty
	 */
	public Optional<Seat.SeatOccupancy> getOccupancy(Seat seat) {
		return Optional.ofNullable(seats.get(seat));
	}

	/**
	 * Prüft, ob der Sitzplatz (Reihe, Position) in der Vorführung vorhanden ist (indirekt abhänig vom Kinosaal)
	 */
	public boolean containsSeat(int row, int pos) {
		return getSeat(row, pos).isPresent();
	}

	/**
	 * Prüft, ob der Sitzplatz in der Vorführung vorhanden ist (indirekt abhänig vom Kinosaal)
	 */
	public boolean containsSeat(Seat seat) {
		return this.seats.containsKey(seat);
	}

	/**
	 * @return max. Anzahl an Plätzen in der Vorführung (indirekt abhängig vom Kinosaal)
	 */
	public int getSeatCount() {
		return this.seats.size();
	}

	/**
	 * @return Anzahl an Plätzen in der Vorführung nach Belebtheit-Status
	 */
	public int getSeatCount(Seat.SeatOccupancy occupancy) {
		return (int) this.seats.values().stream().filter(o -> o .equals(occupancy)).count();
	}

	/**
	 * @return Anzahl an belegten PLätzen (Reserviert + gekauft)
	 */
	public int getSeatProvenCount(Seat.SeatOccupancy occupancy) {
		return getSeatCount() - getSeatCount(Seat.SeatOccupancy.FREE);
	}

	/**
	 * @return Prozentualer Belegten-Status-Anteil
	 */
	public double getPercentageSeatShare(Seat.SeatOccupancy occupancy) {
		int seatCount = getSeatCount();
		if(seatCount == 0) return 0.0;

		return getSeatCount(occupancy) / (double) seatCount;
	}

	/**
	 * @return Prozentualer Anteil an belegten Plätzen (Reserviert + gekauft)
	 */
	public double getPercentageSeatProvenShare() {
		return 1.0 - getPercentageSeatShare(Seat.SeatOccupancy.FREE);
	}

	/**
	 * Setzt neue Platzbelegung für die Kino-Vorführung
	 * @param row Reihe des Platzes beginnend bei index 0
	 * @param pos Position des Platzes in jeder Reihe beginnend bei index 0, max. 99
	 * @param occupancy Platzbelegung für den Platz
	 * @throws EntityNotFoundException wenn der Sitzplatz in der Vorführung nicht vorhanden ist.
	 */
	void setOccupancy(int row, int pos, Seat.SeatOccupancy occupancy) {
		Seat seat = getSeat(row, pos).
			orElseThrow(() -> new EntityNotFoundException("Seat ist in der Vorführung (Abhängig vom Kinosaal) nicht vorhanden!"));
		this.seats.put(seat, occupancy);
	}

	/**
	 * Setzt neue Platzbelegung für die Kino-Vorführung
	 * @param seat der zu ändernde Platz
	 * @param occupancy Platzbelegung für den Platz
	 * @throws EntityNotFoundException wenn der Sitzplatz in der Vorführung nicht vorhanden ist.
	 */
	void setOccupancy(Seat seat, Seat.SeatOccupancy occupancy) {
		if(!seats.containsKey(seat))
			throw new EntityNotFoundException("Seat ist in der Vorführung (Abhängig vom Kinosaal) nicht vorhanden!");

		this.seats.put(seat, occupancy);
	}

	void initSeats(final Map<Seat, Seat.SeatOccupancy> seats) {
		this.seats.putAll(seats);
	}

	void setCinemaHall(CinemaHall cinemaHall) {
		this.cinemaHall = cinemaHall;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getStartDateTime(),
                getBasePrice(), getFilm());
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(!(object instanceof CinemaShow cinemaShow))
			return false;

		return Objects.equals(getId(), cinemaShow.getId()) &&
				Objects.equals(getStartDateTime(), cinemaShow.getStartDateTime()) &&
				Objects.equals(getBasePrice(), cinemaShow.getBasePrice()) &&
				Objects.equals(getFilm(), cinemaShow.getFilm());
	}

	@Override
	public int compareTo(CinemaShow cinemaShow) {
		return this.getStartDateTime().compareTo(cinemaShow.getStartDateTime());
	}
}
