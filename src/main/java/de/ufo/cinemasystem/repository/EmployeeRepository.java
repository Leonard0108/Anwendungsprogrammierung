package de.ufo.cinemasystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.EmployeeEntry;
import de.ufo.cinemasystem.models.UserEntry;
import de.ufo.cinemasystem.models.UserEntry.UserIdentifier;

/**
 * CRUD-Repository für Mitarbeiter
 * @author Lukas Dietrich
 */
@Repository
public interface EmployeeRepository extends CrudRepository<EmployeeEntry, UserIdentifier> {
    /**
     * Finde alle Mitarbeiter.
     * @return Liste aller Mitarbeiter
     */
	@NotNull
	@Override
	List<EmployeeEntry> findAll();
        /**
         * Finde einen Mitarbeiter anhand der Job-Email.
         * @param jobMail die job-Email.
         * @return ein leeres Optional, falls dieser Mitarbeiter nicht existiert, sonst mit Eintrag gefüllt
         */
	Optional<EmployeeEntry> findByJobMail(String jobMail);
        /**
         * Finde einen Mitarbeiter anhand der ID.
         * @param id ID.
         * @return ein leeres Optional, falls dieser Mitarbeiter nicht existiert, sonst mit Eintrag gefüllt
         */
	@NotNull
	Optional<EmployeeEntry> findById(@NotNull UserEntry.UserIdentifier id);
        /**
         * Finde einen Mitarbeiter anhand der ID.
         * @param identifier ID.
         * @return ein leeres Optional, falls dieser Mitarbeiter nicht existiert, sonst mit Eintrag gefüllt
         */
	Optional<EmployeeEntry> findByIdIdentifier(UUID identifier);
}
