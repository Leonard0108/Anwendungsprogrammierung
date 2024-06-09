package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.Seat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

public interface SeatRepository extends CrudRepository<Seat, Long> {

	default Optional<Seat> findByRowPos(int row, int pos) {
		return findById(row * 100L + pos);
	}
	Streamable<Seat> findAll();
}
