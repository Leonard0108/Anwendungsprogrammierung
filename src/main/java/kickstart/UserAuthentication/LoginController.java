package kickstart.UserAuthentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(path = "/lunar_space_port")
public class LoginController {
	private UserRepository userRepository;



	public LoginController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}



	@GetMapping(path = "/register")
	String register() {
		return "welcome";
		//userRepository.findByEmail()
	}


	@GetMapping(path = "/login")
	String login() {
		return "welcome";
		//userRepository.findByEmail()
	}


	@GetMapping(path = "/isLoggedIn")
	String isLoggedIn() {
		return "welcome";
	}


	@GetMapping(path = "/logOut")
	String logout() {
		return "welcome";
	}
}
