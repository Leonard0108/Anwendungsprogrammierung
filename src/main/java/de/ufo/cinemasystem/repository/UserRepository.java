package de.ufo.cinemasystem.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.ufo.cinemasystem.models.UserEntry;
import de.ufo.cinemasystem.models.UserEntry.UserIdentifier;

import java.util.Optional;
import java.util.UUID;


@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<UserEntry, UserIdentifier> {
	@NotNull
	@Override
	Streamable<UserEntry> findAll();
	@NotNull
	Optional<UserEntry> findById(@NotNull UserIdentifier id);
	@NotNull
	UserEntry findByIdAndFirstName(@NotNull UserIdentifier id, String firstName);
	UserEntry findByUserAccountUsername(String userName);
	UserEntry findByUserAccountEmail(String email);
	UserEntry findByeMail(String email);

	//	UserEntry findByUserAccountUsername(String userName);
	//Optional<UserEntry> findUserEntriesByUserAccountUsername(Long aLong);
	Optional<UserEntry> findByIdIdentifier(UUID identifier);
}
