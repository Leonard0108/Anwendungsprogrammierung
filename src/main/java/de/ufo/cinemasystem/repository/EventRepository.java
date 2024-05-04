package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Event;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
	Streamable<Event> findAll();
}
