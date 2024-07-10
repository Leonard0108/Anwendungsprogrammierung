package de.ufo.cinemasystem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * CRUD-Repository for Events.
 * @author Tobias Knoll
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    /**
     * Finde Events im Angegebenen Zeitraum.
     * @param fromDateTime Startzeitpunkt
     * @param toDateTime Endzeitpunkt
     * @return Streamable von Events
     */
	@Query("SELECT cs FROM Event cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime")
	Streamable<Event> findEventInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime);

        /**
         * Finde Events am angegebenen Tag.
         * @param date Tag
         * @return Streamable von Events
         */
	default Streamable<Event> findEventShowsOnDay(LocalDate date) {
		return findEventInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59)
		);
	}


        /**
         * Finde Alle Events.
         * @return Alle Events
         */
	Streamable<Event> findAll();
        /**
         * Finde ein Event anhand des Namens
         * @param name Eventname
         * @return Optional, ggf. leer.
         */
	Optional<Event> findByName(String name);
}
