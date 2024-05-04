package kickstart.repository;

import kickstart.models.UserEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;


@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<UserEntry, Long> {
	Optional<UserEntry> findByEmail(String email);
	UserEntry findByEmailAndPassword(String email, String password);
}
