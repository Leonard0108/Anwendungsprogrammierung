
package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Film;

import java.time.LocalDateTime;

/**
 * Crud-Repository for saving films.
 * @author Jannik Schwaß, Yannick Harnisch
 */
@Repository
public interface FilmRepository extends CrudRepository<Film, Long>{

        /**
         * Find all films in the system.
         * @return a streamable of all films
         */
	@Override
	Streamable<Film> findAll();
        /**
         * Find all films available at a date.
         * @param dateTime the date.
         * @return all films meeting the criteria.
         */
	default Streamable<Film> findAvailableAt(LocalDateTime dateTime) {
		return findAll().filter(f -> f.isAvailableAt(dateTime));
	}

        /**
         * find all films that are currently {@link de.ufo.cinemasystem.models.Film#isAvailableNow() available}
         * @return a streamable of films
         */
	default Streamable<Film> findAvailableNow() {
		return findAll().filter(Film::isAvailableNow);
	}
}
