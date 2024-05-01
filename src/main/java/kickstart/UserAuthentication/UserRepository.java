package kickstart.UserAuthentication;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntry, Long> {
	UserEntry findByUsername(String username);
	UserEntry findByEmail(String email);
	UserEntry findByUsernameAndPassword(String username, String password);
	UserEntry findByEmailAndPassword(String email, String password);
}
