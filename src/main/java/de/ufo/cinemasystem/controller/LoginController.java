package de.ufo.cinemasystem.controller;



import de.ufo.cinemasystem.additionalfiles.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import de.ufo.cinemasystem.repository.UserRepository;


@Controller
@RequestMapping(path = "/lunar_space_port")
public class LoginController {
	UserRepository  userRepository;
	//PasswordEncoder passwordEncoder;
	UserService     userService;


	public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
		this.userRepository = userRepository;
		//this.passwordEncoder = passwordEncoder;
		this.userService = userService;
	}




	@GetMapping(path = "/registration")
	public String registration() {
		return "registration";
	}


	@PostMapping(path = "/register", consumes = "application/json")
	String register(@RequestBody RegistrationRequest registrationRequest) {
		/*userService.signUp(registrationRequest.email, registrationRequest.password, registrationRequest.forename, registrationRequest.name,
			registrationRequest.streetAddress, registrationRequest.houseNumber, registrationRequest.city, registrationRequest.state, registrationRequest.country,
			registrationRequest.phoneNumber);*/
		return "redirect:/lunar_space_port/register";
	}


	@GetMapping(path = "/login")
	String login() {
		return "login";
	}




	@PostMapping(path = "/test")
	String test() {
		return "welcome";
	}




	@PostMapping(path = "/checkLoginData", consumes = {"application/json"})
	String checkLoginData(@RequestBody SignInRequest signInRequest)
	{
		/*if (signInRequest.email == null || signInRequest.password == null)
		{
			return "redirect:/lunar_space_port/login";
		}

		/*if (userRepository.findByEmail(signInRequest.email).isPresent())
		{
			System.out.println("User is already logged in.");
			return "redirect:/lunar_space_port/test";
		}*/

		//userService.login(signInRequest.email, signInRequest.password);
		System.out.println("Nutzer wurde angemeldet.");

		return "welcome";
	}




	@GetMapping(path = "/isLoggedIn", consumes = {"application/json"})
	String isLoggedIn(SignInRequest signInRequestRequest) {
		/*if (userRepository.findByEmail(signInRequestRequest.email).isPresent())
		{
			System.out.println("User is already logged in.");
			return "welcome";
		}*/
		return "redirect:/lunar_space_port/login";
	}




	@PostMapping(path = "/logOut")
	String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate(); // Ungültig machen der aktuellen Session
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					cookie.setMaxAge(0); // Löschen aller Cookies
				}
			}
		}
		return "redirect:/lunar_space_port/login";
	}




	@Getter
	static class RegistrationRequest {
		private String email;
		private String password;
		private String forename;
		private String name;
		private String streetAddress;
		private Long   houseNumber;
		private String city;
		private String state;
		private String country;
		private String phoneNumber;
	}


	@Getter
	static class SignInRequest {
		private String email;
		private String password;
	}
}


