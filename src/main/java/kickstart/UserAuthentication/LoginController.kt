package kickstart.UserAuthentication




import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class LoginController () //private val userRepository: UserRepository)
{
    @GetMapping(path = ["/register"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(): String {
        return "welcome"
        //userRepository.findByEmail()
    }


    @GetMapping(value = ["/login"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun login() {
        //userRepository.findByEmail()
    }


    @GetMapping(path = ["/isLoggedIn"], produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun isLoggedIn(): String {
        return "welcome"
    }


    @GetMapping(path = ["/logOut"])
    fun logout(): String = "welcome"
}