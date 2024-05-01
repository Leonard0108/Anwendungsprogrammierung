package kickstart.repository;

import kickstart.models.UserEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntry, Long> {
	UserEntry findByEmail(String email);
	UserEntry findByEmailAndPassword(String email, String password);
}
