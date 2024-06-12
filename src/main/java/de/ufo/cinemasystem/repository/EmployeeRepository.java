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

@Repository
public interface EmployeeRepository extends CrudRepository<EmployeeEntry, UserIdentifier> {
	@NotNull
	@Override
	List<EmployeeEntry> findAll();
	Optional<EmployeeEntry> findByJobMail(String jobMail);
	@NotNull
	Optional<EmployeeEntry> findById(@NotNull UserEntry.UserIdentifier id);
	Optional<EmployeeEntry> findByIdIdentifier(UUID identifier);
}
