
package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.CinemaShow;
import org.springframework.data.repository.CrudRepository;

import de.ufo.cinemasystem.models.Reservation;
import de.ufo.cinemasystem.models.UserEntry;
import org.springframework.data.util.Streamable;
import org.springframework.data.jpa.repository.Query;

/**
 * Crud repository for saving reservations.
 * @author Jannik Schwaß
 * @version 1.0
 */
public interface ReservationRepository extends CrudRepository<Reservation, Long>{
    
    /**
     * Find all reservations by a given user.
     * @param who the user to retrieve reservations for 
     * @return a streamable of all reservations
     */
    @Query("SELECT r FROM Reservation r WHERE r.reservingAccount= :who")
    Streamable<Reservation> findAllByUser(UserEntry who);

	@Query("SELECT r FROM Reservation r WHERE r.cinemaShow= :where")
	Streamable<Reservation> findAllByCinemaShow(CinemaShow where);
}
