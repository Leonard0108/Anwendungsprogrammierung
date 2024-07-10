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
import org.springframework.security.access.prepost.PreAuthorize;


/**
 * Controller, welcher Logins und Nutzerregistrierungen entgegennimmt.
 */
@Controller
public class LoginController {
	UserRepository  userRepository; //Repository, welches die Datenbankschnittstelle darstellt.
	UserService     userService;    //Service, welcher das Backend, also check der eingegebenen Daten und entsprechenden
	                                //Rückgabewert liefert.


        /**
         * Erstelle einen neuen Controller mit den angegebenen Abhängigkeiten.
         * @param userRepository Implementierung Nutzerrepository
         * @param userService Nutzerservice.
         */
	public LoginController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}


        /**
         * GET-Endpunkt der Registrierung.
         * @return "redirect:/registration"
         */
	@GetMapping("/register")
	public String register()
	{
		return "redirect:/registration";
	}


        /**
         * POST-Endpunkt der Registrierung.
         * @param form Ausgefülltes Formular
         * @param result Ergebnis der Fehlerprüfung
         * @param redirectAttributes Redirect-Modell
         * @param model Modell
         * @return Template-Name
         */
	@PostMapping("/registration")
	String register(@Valid RegistrationForm form, Errors result, RedirectAttributes redirectAttributes, Model model) {
		short creationResult;



		if (result.hasErrors()) {
			//System.out.println(result.getAllErrors());
			return "registration";
		}

		System.out.println(form);

		creationResult = userService.createUser(form);

		//Fehlerbehandlung des Rückgabewerts des Services.
		switch (creationResult) {
			case 0:
				redirectAttributes.addFlashAttribute("createdUser", "Ein neuer Nutzer wurde erfolgreich angelegt");
				return "redirect:/login";
			case 1:
				model.addAttribute("error", "Diese E-Mail-Adresse wird bereits verwendet!.");
				break;
			case 2:
				model.addAttribute("error", "Dieser Benutzername ist bereits vergeben.");
				break;
			case 3:
				model.addAttribute("error", "Unbekannter E-Mail-Provider. Bitte Schreibweise prüfen.");
				break;
			case 4:
				model.addAttribute("error", "Ungültige Postleitzahl!");
				break;
		}

		model.addAttribute("employeeRegistrationForm", form);
		return "registration";
	}

        /**
         * GET-Endpunkt der Registrierung.
         * @param m Modell
         * @param form Formular
         * @return "redirect:/registration"
         */
	@GetMapping("/registration")
	String register(Model m, RegistrationForm form) {
            m.addAttribute("title", "Registrieren");
		return "registration";
	}

        /**
         * GET-Endpunkt: Liste alle Nutzer auf.
         * @param model Modell
         * @return "welcome"
         */
	@GetMapping("/customers")
	@PreAuthorize("hasRole('BOSS')")
	String customers(Model model) {
		List<UserEntry> userEntries = userService.findAll().toList();
		System.out.println(userEntries);
		model.addAttribute("customerList", userEntries);
                model.addAttribute("title", "Registrieren");
		return "welcome";
	}

        /**
         * Rollentest-Endpunkt
         * @param m Modell
         * @return "roletest"
         */
	@GetMapping("/role")
	String getRole(Model m) {
                m.addAttribute("title", "Rollencheck");
		return "roletest";
	}

        /**
         * GET-Endpunkt: logout
         * @param request request
         * @param session aktuelle sitzung
         * @return "redirect:/"
         * @throws ServletException wenn der logout fehlschlägt
         */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpSession session) throws ServletException {
		request.logout();
		session.invalidate();
		return "redirect:/";
	}



}


