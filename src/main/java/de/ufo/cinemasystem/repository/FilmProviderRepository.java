package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.FilmProvider;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface FilmProviderRepository extends CrudRepository<FilmProvider, Long> {
	@Override
	Streamable<FilmProvider> findAll();
}
