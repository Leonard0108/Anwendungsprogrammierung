package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.FilmProvider;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
 * CRUD-Repository fpr Film Providers.
 * @author Yannick Harnisch
 */
public interface FilmProviderRepository extends CrudRepository<FilmProvider, Long> {
    /**
     * Find all film providers.
     * @return streamable of all film providers.
     */
	@Override
	Streamable<FilmProvider> findAll();
}
