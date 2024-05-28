/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Film;

import java.time.LocalDateTime;

/**
 *
 * @author Jannik
 */
@Repository
public interface FilmRepository extends CrudRepository<Film, Long>{
	
	@Override
	Streamable<Film> findAll();
	default Streamable<Film> findAvailableAt(LocalDateTime dateTime) {
		return findAll().filter(f -> f.isAvailableAt(dateTime));
	}

	default Streamable<Film> findAvailableNow() {
		return findAll().filter(Film::isAvailableNow);
	}
}
