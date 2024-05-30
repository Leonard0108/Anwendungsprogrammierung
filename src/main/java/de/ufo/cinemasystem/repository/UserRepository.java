package de.ufo.cinemasystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.ufo.cinemasystem.models.UserEntry;


@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<UserEntry, Long> {
	@Override
	Streamable<UserEntry> findAll();
	UserEntry findByUserAccountUsername(String userName);
	//	UserEntry findByUserAccountUsername(String userName);
	//Optional<UserEntry> findUserEntriesByUserAccountUsername(Long aLong);
}
