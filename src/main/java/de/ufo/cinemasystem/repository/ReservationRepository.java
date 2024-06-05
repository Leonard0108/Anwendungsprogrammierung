/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;

import de.ufo.cinemasystem.models.Reservation;
import de.ufo.cinemasystem.models.UserEntry;
import org.springframework.data.util.Streamable;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Jannik
 */
public interface ReservationRepository extends CrudRepository<Reservation, Long>{
    
    @Query("SELECT r FROM Reservation r WHERE r.reservingAccount= :who")
    Streamable<Reservation> findAllByUser(UserEntry who);
}
