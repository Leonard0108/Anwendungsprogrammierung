package kickstart.models;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "CINEMA_HALLS")
public class CinemaHall {

	@Id @GeneratedValue
	private Long id;
	private String name;
	private int numberOfPlaces;
	@OneToMany(cascade = CascadeType.ALL)
	private final SortedSet<Platz> places;


	public CinemaHall(String name, final Collection<Platz> places) {
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

	public int getNumberOfPlaces() {
		return this.numberOfPlaces;
	}

	public Iterable<Platz> getPlaces() {
		return this.places;
	}
}
