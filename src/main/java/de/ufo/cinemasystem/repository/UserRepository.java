package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.ufo.cinemasystem.models.UserEntry;

import java.util.Optional;


@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<UserEntry, Long> {
	//Optional<UserEntry> findByEmail(String email);
	@Override
	Streamable<UserEntry> findAll();
}
