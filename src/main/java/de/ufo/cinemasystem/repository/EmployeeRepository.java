package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.EmployeeEntry;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends CrudRepository<EmployeeEntry, Long> {
	@NotNull
	@Override
	List<EmployeeEntry> findAll();
	Optional<EmployeeEntry> findByJobMail(String jobMail);
}
