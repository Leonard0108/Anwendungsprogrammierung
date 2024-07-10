package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.Seat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

/**
 * CRUD-Repository of seats
 * @author Tobias Knoll
 */
public interface SeatRepository extends CrudRepository<Seat, Long> {

    /**
     * Find a seat, by its row and position.
     * @param row row (0-25)
     * @param pos position (0-99)
     * @return optional, with the seat if it exists in the db, false otherwise.
     */
	default Optional<Seat> findByRowPos(int row, int pos) {
		return findById(row * 100L + pos);
	}
        /**
         * Get all seats in the system.
         * @return Streamable of Seats
         */
	Streamable<Seat> findAll();
}
