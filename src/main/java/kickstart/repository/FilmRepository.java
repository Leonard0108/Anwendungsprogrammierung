/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package kickstart.repository;

import kickstart.models.Film;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Jannik
 */
public interface FilmRepository extends CrudRepository<Film, Long>{
    
}
