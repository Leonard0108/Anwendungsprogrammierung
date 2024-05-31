package de.ufo.cinemasystem.repository;

import org.salespointframework.catalog.Product.ProductIdentifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Snacks;

@Repository
public interface SnacksRepository extends CrudRepository<Snacks, ProductIdentifier> {
    Streamable<Snacks> findAll();
}