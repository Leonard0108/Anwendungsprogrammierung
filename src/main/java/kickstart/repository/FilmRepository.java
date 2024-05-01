/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package kickstart.repository;

import kickstart.models.CinemaShow;
import kickstart.models.Film;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Jannik
 */
@Repository
public interface FilmRepository extends CrudRepository<Film, Long>{
	Streamable<Film> findAll();
}
