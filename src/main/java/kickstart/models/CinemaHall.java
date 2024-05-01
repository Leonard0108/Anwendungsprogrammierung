package kickstart.models;

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
	private final Map<Seat, Seat.SeatOccupancy> seats;

	@OneToMany(mappedBy = "cinemaHall", cascade =  CascadeType.ALL)
	private final SortedSet<CinemaShow> cinemaShows = new TreeSet<>();

	//@OneToMany(cascade = CascadeType.ALL)
	//private final HashSet<Event> events = new HashSet<>();

	public CinemaHall(String name/*, final Map<Seat, Seat.SeatOccupancy> seats*/) {
		this.name = name;
		this.numberOfPlaces = 0;/*seats.size();*/
		this.seats = new TreeMap<>();
		//this.seats = new TreeMap<>(seats);
	}

	public CinemaHall() {
		this.seats = new TreeMap<>();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public int getNumberOfPlaces() { return this.numberOfPlaces; }

	public Streamable<Map.Entry<Seat, Seat.SeatOccupancy>> getSeats() {
		return Streamable.of(this.seats.entrySet());
	}

	// siehe: https://www.tabnine.com/code/java/methods/org.springframework.data.util.Streamable/of
	public Streamable<CinemaShow> getCinemaShows() {
		return Streamable.of(this.cinemaShows);
	}

	/*
	public Streamable<Event> getEvents() {
		return Streamable.of(this.events);
	}
	 */

	public void addCinemaShow(CinemaShow newCinemaShow) {
		if(cinemaShows.contains(newCinemaShow)) return;

		this.cinemaShows.add(newCinemaShow);
		newCinemaShow.setCinemaHall(this);
	}

	/*
	void addEvent(Event newEvent) {
		this.events.add(newEvent);
	}
	 */

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
