package kickstart.UserAuthentication




import org.springframework.stereotype.Repository




@Repository
interface UserRepository//: CrudRepository<UserEntry?, String?>
{
    fun findByEmail(username: String): UserRepository?
}
