package de.ufo.cinemasystem.controller;



import de.ufo.cinemasystem.additionalfiles.RegistrationForm;
import de.ufo.cinemasystem.services.UserService;
import de.ufo.cinemasystem.models.UserEntry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import de.ufo.cinemasystem.repository.UserRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class LoginController {
	UserRepository  userRepository;
	UserService     userService;


	public LoginController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}




	@PostMapping("/registration")
	String register(@Valid RegistrationForm form, Errors result, RedirectAttributes redirectAttributes) {
		short creationResult;



		if (result.hasErrors()) {
			System.out.println(result.getAllErrors());
			return "registration";
		}

		System.out.println(form);

		creationResult = userService.createUser(form);

		switch (creationResult) {
			case 0:
				redirectAttributes.addFlashAttribute("createdUser", "Ein neuer Nutzer wurde erfolgreich angelegt");
				break;
			case 1:
				redirectAttributes.addFlashAttribute("error", "User creation failed. E-mail already exists.");
				break;
			case 2:
				redirectAttributes.addFlashAttribute("error", "User name already exists.");
				break;
			case 3:
				redirectAttributes.addFlashAttribute("error", "E-mail provider could not be found");
		}



		return "redirect:/login";
	}


	@GetMapping("/registration")
	String register(Model m, RegistrationForm form) {
            m.addAttribute("title", "Registrieren");
		return "registration";
	}




	/*
	@PostMapping("/login")
	String login(@Valid LoginForm form, Errors result, HttpSession session, RedirectAttributes redirectAttributes) {

		return "redirect:/";
	}


	@GetMapping(path = "/login")
	String login() {
		return "login";
	}
	*/



	@GetMapping("/customers")
	//@PreAuthorize("hasRole('BOSS')")
	String customers(Model model) {
		List<UserEntry> userEntries = userService.findAll().toList();
		System.out.println(userEntries);
		model.addAttribute("customerList", userEntries);
                model.addAttribute("title", "Registrieren");
		return "welcome";
	}

	@GetMapping("/role")
	String getRole(Model m) {
                m.addAttribute("title", "Rollencheck");
		return "roletest";
	}


	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpSession session) throws ServletException {
		request.logout();
		session.invalidate();
		return "redirect:/";
	}



}


