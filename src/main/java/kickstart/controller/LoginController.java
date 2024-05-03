package kickstart.controller;



import kickstart.Application;
import kickstart.models.UserEntry;
import kickstart.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;





@Controller
@RequestMapping(path = "/lunar_space_port")
public class LoginController {
	private final Application application;
	UserRepository userRepository;
	PasswordEncoder passwordEncoder;


	public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, Application application) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.application = application;
	}




	@GetMapping(path = "/registration")
	public String registration() {
		return "welcome";
	}


	@PostMapping(path = "/register", consumes = "application/json")
	String register(@RequestBody RegistrationRequest registrationRequest) {
		if (userRepository.findByEmail(registrationRequest.email) == null)
		{
			UserEntry newUser = new UserEntry();
			newUser.setEmail(registrationRequest.getEmail());
			newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
			userRepository.save(newUser);


			System.out.println(userRepository.findByEmail(registrationRequest.email).getEmail());
			return "redirect:/lunar_space_port/isLoggedIn"; // Redirect to login page
		}
		else
		{
			return "registration";
		}
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




	static class RegistrationRequest {
		private String email;
		private String password;



		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}


