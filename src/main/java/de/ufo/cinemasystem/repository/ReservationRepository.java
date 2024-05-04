/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;

import de.ufo.cinemasystem.models.Reservation;

/**
 *
 * @author Jannik
 */
public interface ReservationRepository extends CrudRepository<Reservation, Long>{
    
}
