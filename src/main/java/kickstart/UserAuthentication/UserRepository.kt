package kickstart.UserAuthentication;




import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;




@Repository
interface UserRepository extends CrudRepository<UserEntry, String> {
}
