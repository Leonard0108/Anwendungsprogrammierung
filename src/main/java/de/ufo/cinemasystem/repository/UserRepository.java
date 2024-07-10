package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.EmployeeEntry;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.ufo.cinemasystem.models.UserEntry;
import de.ufo.cinemasystem.models.UserEntry.UserIdentifier;

import java.util.Optional;
import java.util.UUID;

/**
 * CRUD-Repository for user entries.
 * @author Lukas Dietrich
 */
@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<UserEntry, UserIdentifier> {
    /**
     * find all users.
     * @return streamable of all UserEntries
     */
	@NotNull
	@Override
	Streamable<UserEntry> findAll();
        /**
         * Find a user by his id.
         * @param id the id 
         * @return optional with UserEntry or null if there is no user with that id
         */
	@NotNull
	Optional<UserEntry> findById(@NotNull UserIdentifier id);
        /**
         * Find a user by his id.
         * @param id the id 
     * @param firstName the first name
         * @return optional with UserEntry or null if there is no user with that values
         */
	@NotNull
	UserEntry findByIdAndFirstName(@NotNull UserIdentifier id, String firstName);
        /**
         * Find a user by his name.
         * @param userName the name 
         * @return optional with UserEntry or null if there is no user with that name
         */
	UserEntry findByUserAccountUsername(String userName);
        /**
         * Find a user by his email.
         * @param email the email 
         * @return optional with UserEntry or null if there is no user with that email
         */
	UserEntry findByUserAccountEmail(String email);
        /**
         * Find a user by his email.
         * @param email the email 
         * @return optional with UserEntry or null if there is no user with that email
         */
	UserEntry findByeMail(String email);

	//	UserEntry findByUserAccountUsername(String userName);
	//Optional<UserEntry> findUserEntriesByUserAccountUsername(Long aLong);
        /**
         * Find a user by his id.
         * @param identifier the id 
         * @return optional with UserEntry or null if there is no user with that id
         */
	Optional<UserEntry> findByIdIdentifier(UUID identifier);
}
