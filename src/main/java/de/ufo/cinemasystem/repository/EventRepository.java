package de.ufo.cinemasystem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

	@Query("SELECT cs FROM Event cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime")
	Streamable<Event> findEventInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime);

	default Streamable<Event> findEventShowsOnDay(LocalDate date) {
		return findEventInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59)
		);
	}


	Streamable<Event> findAll();
	Optional<Event> findByName(String name);
}
