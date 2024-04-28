package kickstart.models;

import jakarta.persistence.*;
import jdk.jfr.Event;

import java.util.*;

@Entity
@Table(name = "CINEMA_HALLS")
public class CinemaHall {

	@Id @GeneratedValue
	private Long id;
	private String name;
	private int numberOfPlaces;
	@OneToMany(cascade = CascadeType.ALL)
	private final SortedSet<Seat> places;

	@OneToMany(cascade = CascadeType.ALL)
	private final SortedSet<CinemaShow> cinemaShows = new TreeSet<>();

	@OneToMany(cascade = CascadeType.ALL)
	private final HashSet<EventStub> events = new HashSet<>();

	public CinemaHall(String name, final Collection<Seat> places) {
		this.name = name;
		this.numberOfPlaces = places.size();
		this.places = new TreeSet<>(places);
	}

	public CinemaHall() {
		this.places = new TreeSet<>();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public int getNumberOfPlaces() { return this.numberOfPlaces; }

	public Iterable<Seat> getPlaces() {
		return this.places;
	}

	public Iterable<CinemaShow> getCinemaShows() {
		return this.cinemaShows;
	}

	public Iterable<EventStub> getEvents() {
		return this.events;
	}

	void addCinemaShow(CinemaShow newCinemaShow) {
		if(cinemaShows.contains(newCinemaShow)) return;

		this.cinemaShows.add(newCinemaShow);
	}

	void addEvent(EventStub newEvent) {
		this.events.add(newEvent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), getNumberOfPlaces(),
			this.places, this.cinemaShows, this.events);
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(!(object instanceof CinemaHall cinemaHall))
			return false;

		return Objects.equals(getId(), cinemaHall.getId()) &&
			Objects.equals(getName(), cinemaHall.getName()) &&
			Objects.equals(getNumberOfPlaces(), cinemaHall.getNumberOfPlaces()) &&
			Objects.equals(this.places, cinemaHall.places) &&
			Objects.equals(this.cinemaShows, cinemaHall.cinemaShows) &&
			Objects.equals(this.events, cinemaHall.events);
	}


}
