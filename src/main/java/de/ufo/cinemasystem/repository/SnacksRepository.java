package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import de.ufo.cinemasystem.models.Snacks;

public interface SnacksRepository extends CrudRepository<Snacks, Long> {
    Streamable<Snacks> findAll();
}