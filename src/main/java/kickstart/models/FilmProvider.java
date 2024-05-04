package kickstart.models;

import jakarta.persistence.*;
import org.springframework.data.util.Streamable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "FILM_PROVIDERS")
public class FilmProvider{

	@Id @GeneratedValue
	private Long id;

	// ein Anbieter bietet mehrere Filme an
	@OneToMany
	private Set<Film> films = new HashSet<>();
	private String name;

	public void addFilm(Film film) {
		if(films.contains(film)) return;

		this.films.add(film);
		//film.setFilmProvider(this);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public Streamable<Film> getFilms() {
		return Streamable.of(this.films);
	}

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
