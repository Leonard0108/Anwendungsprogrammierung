package de.ufo.cinemasystem.models;

import jakarta.persistence.*;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;

import java.util.*;
import java.util.stream.Stream;

// https://www.baeldung.com/hibernate-one-to-many
@Entity
@Table(name = "CINEMA_HALLS")
public class CinemaHall {

	@Id @GeneratedValue
	private Long id;
	private String name;
	private int numberOfPlaces = 0;
	@ElementCollection
	private final Map<Seat, Seat.PlaceGroup> seats = new TreeMap<>();

	@OneToMany(mappedBy = "cinemaHall", cascade =  CascadeType.ALL)
	private final SortedSet<CinemaShow> cinemaShows = new TreeSet<>();

	@OneToMany(mappedBy = "cinemaHall", cascade = CascadeType.ALL)
	private final SortedSet<Event> events = new TreeSet<>();

	CinemaHall(String name, final Map<Seat, Seat.PlaceGroup> seats) {
		this.name = name;
		this.numberOfPlaces = 0;/*seats.size();*/
		this.seats.putAll(seats);
		//this.seats = new TreeMap<>(seats);
	}

	public CinemaHall() {}

	public Long getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public int getNumberOfPlaces() { return this.numberOfPlaces; }

	/**
	 * @return alle Sitzplätze und die dazugehörigen Platzgruppen des Kinosaals
	 */
	public Streamable<Map.Entry<Seat, Seat.PlaceGroup>> getSeatsAndPlaceGroups() {
		return Streamable.of(this.seats.entrySet());
	}

	/**
	 * @param row Reihe des Platzes beginnend bei index 0
	 * @param pos Position des Platzes in jeder Reihe beginnend bei index 0, max. 99
	 * @return Sitz und Platzgruppe, wenn der Platz an der Stelle im Kinosaal vorhanden ist, sonst empty
	 */
	public Optional<Map.Entry<Seat, Seat.PlaceGroup>> getSeatAndPlaceGroup(int row, int pos) {
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
	 * @return Platzgruppe, wenn der Platz an der Stelle im Kinosaal vorhanden ist, sonst empty
	 */
	public Optional<Seat.PlaceGroup> getPlaceGroup(int row, int pos) {
		return getSeat(row, pos).map(seats::get);
	}

	/**
	 * @param seat Sitzplatz an einer Stelle
	 * @return Platzgruppe, wenn der Platz an der Stelle im Kinosaal vorhanden ist, sonst empty
	 */
	public Optional<Seat.PlaceGroup> getPlaceGroup(Seat seat) {
		return Optional.ofNullable(seats.get(seat));
	}

	// siehe: https://www.tabnine.com/code/java/methods/org.springframework.data.util.Streamable/of
	public Streamable<CinemaShow> getCinemaShows() {
		return Streamable.of(this.cinemaShows);
	}

	public Streamable<Event> getEvents() {
		return Streamable.of(this.events);
	}


	public void addCinemaShow(CinemaShow newCinemaShow) {
		if(cinemaShows.contains(newCinemaShow)) return;

		this.cinemaShows.add(newCinemaShow);
		newCinemaShow.setCinemaHall(this);
	}


	public void addEvent(Event newEvent) {
		if(events.contains(newEvent)) return;

		this.events.add(newEvent);
		newEvent.setCinemaHall(this);
	}


	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), getNumberOfPlaces(),
			this.seats, this.cinemaShows/*, this.events*/);
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(!(object instanceof CinemaHall cinemaHall))
			return false;

		return Objects.equals(getId(), cinemaHall.getId()) &&
			Objects.equals(getName(), cinemaHall.getName()) &&
			Objects.equals(getNumberOfPlaces(), cinemaHall.getNumberOfPlaces()) &&
			Objects.equals(this.seats, cinemaHall.seats) &&
			Objects.equals(this.cinemaShows, cinemaHall.cinemaShows) /*&&
			Objects.equals(this.events, cinemaHall.events)*/;
	}
}
