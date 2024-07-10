package de.ufo.cinemasystem.repository;

import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Snacks;

/**
 * Repository saving Snacks.
 * @author Yannick Harnisch
 * @author Simon Liepe
 */
@Repository
public interface SnacksRepository extends Catalog<Snacks> {
	//@Query("SELECT cs FROM Snacks cs WHERE cs.snack = :snack")
	//Streamable<Snacks> findAllByFilm(Snacks snack);

    /**
     * The default sort order for snacks.
     */
	static final Sort DEFAULT_SORT = Sort.sort(Snacks.class).by(Snacks::getId).descending();

        /**
         * find all snacks by type.
         * @param type the type to return
         * @param sort sort order
         * @return Streamable of applicable snacks
         */
	Streamable<Snacks> findByType(Snacks.SnackType type, Sort sort);

        /**
         * find all snacks by type.
         * @param type the type to return
         * @return Streamable of applicable snacks
         */
	default Streamable<Snacks> findByType(Snacks.SnackType type) {
		return findByType(type, DEFAULT_SORT);
	}
}