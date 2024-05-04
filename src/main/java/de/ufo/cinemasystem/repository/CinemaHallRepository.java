package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;

public interface CinemaHallRepository extends CrudRepository<CinemaHall, Long> {
	Streamable<CinemaHall> findAll();
}
