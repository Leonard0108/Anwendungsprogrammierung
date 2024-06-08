/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.ufo.cinemasystem.repository;


import de.ufo.cinemasystem.models.Ticket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
 *
 * @author Jannik
 */
public interface TicketRepository extends CrudRepository<Ticket, Long>{
	Streamable<Ticket> findAll();
}
