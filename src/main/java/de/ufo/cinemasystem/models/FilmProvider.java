package de.ufo.cinemasystem.models;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "FILM_PROVIDERS")
public class FilmProvider{

	@Id @GeneratedValue
	private Long id;

	// ein Anbieter bietet mehrere Filme an
	//@OneToMany
	//private final Set<Film> films = new HashSet<>();
	private String name;

	public FilmProvider(String name) {
		this.name = name;
	}

	// Default Construktor, nicht benutzen!
	public FilmProvider() {}

	/*
	public void addFilm(Film film) {
		if(films.contains(film)) return;

		this.films.add(film);
		//film.setFilmProvider(this);
	}
	 */

	public Long getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	/*
	public Streamable<Film> getFilms() {
		return Streamable.of(this.films);
	}
	 */

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName());
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(!(object instanceof FilmProvider filmProvider))
			return false;

		return Objects.equals(getId(), filmProvider.getId()) &&
			Objects.equals(getName(), filmProvider.getName());
	}
}
