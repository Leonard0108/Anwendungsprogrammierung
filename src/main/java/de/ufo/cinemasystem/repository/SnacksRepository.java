package de.ufo.cinemasystem.repository;

import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.models.Snacks.SnackType;

@Repository
public interface SnacksRepository extends Catalog<Snacks> {

    @Query("SELECT cs FROM Snacks cs WHERE cs.snack = :snack")
    Streamable<Snacks> findAllByFilm(Snacks snack);

    static final Sort DEFAULT_SORT = Sort.sort(Snacks.class).by(Snacks::getId).descending();

    Streamable<Snacks> findByType(SnackType type, Sort sort);

    default Streamable<Snacks> findByType(SnackType type) {
        return findByType(type, DEFAULT_SORT);
    }
}