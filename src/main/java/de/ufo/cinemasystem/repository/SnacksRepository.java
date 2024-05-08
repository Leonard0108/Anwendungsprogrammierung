package de.ufo.cinemasystem.repository;

import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import de.ufo.cinemasystem.models.Snacks;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface SnacksRepository extends CrudRepository<Snacks, Long> {
    Streamable<Snacks> findAll();

	Optional<Snacks> findByName(String name);

	boolean existsByName(String name);
}