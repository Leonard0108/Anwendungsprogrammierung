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

import java.util.Optional;


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
		return "registration";
	}


	@PostMapping(path = "/register", consumes = "application/json")
	String register(@RequestBody RegistrationRequest registrationRequest) {
		if (userRepository.findByEmail(registrationRequest.email).isEmpty())
		{
			UserEntry newUser = new UserEntry();
			newUser.setEmail(registrationRequest.email);
			newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
			try {
				userRepository.save(newUser);
				System.out.println(userRepository.findByEmail("lukasd2000@gmx.de").toString());
			}
			catch (Exception e) {
				e.printStackTrace();
			}


			return "welcome";
		}
		else
		{
			return "welcome";
		}
	}


	@GetMapping(path = "/login", consumes = {"application/json"})
	String login() {
		return "login";
	}




	@PostMapping(path = "/checkLoginData")
	String checkLoginData(@RequestBody RegistrationRequest loginRequest)
	{
		Optional<UserEntry> loginUser = userRepository.findByEmail(loginRequest.email);
		String    password  = passwordEncoder.encode(loginRequest.getPassword());


		//password = passwordEncoder.encode(password);

		if (loginUser.isPresent())
		{
			if (passwordEncoder.matches(loginRequest.password, loginUser.get().getPassword()))
			{
				System.out.println(loginUser.get().getEmail());
				System.out.println("Hallo Welt");
				return "welcome";
			}
			System.out.println("Tschüss Welt");
			return "welcome";
		}
		System.out.println(loginUser);
		return "welcome";
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

		public String getPassword() {
			return password;
		}

	}
}


