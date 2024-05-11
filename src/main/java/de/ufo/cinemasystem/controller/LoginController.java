package de.ufo.cinemasystem.controller;



import de.ufo.cinemasystem.additionalfiles.UserService;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import de.ufo.cinemasystem.additionalfiles.*;
import de.ufo.cinemasystem.Application;
import de.ufo.cinemasystem.models.UserEntry;
import de.ufo.cinemasystem.repository.UserRepository;

import java.util.Optional;


@Controller
@RequestMapping(path = "/lunar_space_port")
public class LoginController {
	private final   Application application;
	UserRepository  userRepository;
	PasswordEncoder passwordEncoder;
	UserService     userService;


	public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, Application application, UserService userService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.application = application;
		this.userService = userService;
	}




	@GetMapping(path = "/registration")
	public String registration() {
		return "registration";
	}


	@PostMapping(path = "/register", consumes = "application/json")
	String register(@RequestBody RegistrationRequest registrationRequest) {
		userService.signUp(registrationRequest.email, registrationRequest.password, registrationRequest.forename, registrationRequest.name,
			registrationRequest.streetAddress, registrationRequest.houseNumber, registrationRequest.city, registrationRequest.state, registrationRequest.country,
			registrationRequest.phoneNumber);
		return "redirect:/lunar_space_port/register";
	}


	@GetMapping(path = "/login", consumes = {"application/json"})
	String login() {
		return "login";
	}




	@PostMapping(path = "/checkLoginData")
	String checkLoginData(@RequestBody RegistrationRequest loginRequest)
	{
		Optional<UserEntry> loginUser = userRepository.findByEmail(loginRequest.email);


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
		System.out.println("Hallo Welt");
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
}


