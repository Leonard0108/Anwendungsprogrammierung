package de.ufo.cinemasystem.controller;


import de.ufo.cinemasystem.additionalfiles.EmployeeRegistrationForm;
import de.ufo.cinemasystem.repository.UserRepository;
import de.ufo.cinemasystem.services.EmployeeService;
import de.ufo.cinemasystem.models.EmployeeEntry;
import de.ufo.cinemasystem.models.UserEntry;
import de.ufo.cinemasystem.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;


/**
 * Spring MVC-Controller der Personalverwaltung.
 * @author Lukas Dietrich
 */
@Controller
@RequestMapping(path = "/manage")
public class EmployeeManagementController {
	EmployeeRepository employeeRepo;

	UserRepository userRepo;
	EmployeeService employeeService;


        /**
         * Erstelle einen neuen Controller mit den angegebenen Abhängigkeiten.
         * @param employeeService Mitarbeiterservice
         * @param employeeRepo Implementierung Employee-Repository
         * @param userRepo Implementierung Nutzerrepository.
         */
	EmployeeManagementController(EmployeeService employeeService, EmployeeRepository employeeRepo, UserRepository userRepo) {
		this.employeeService = employeeService;
		this.employeeRepo = employeeRepo;
		this.userRepo = userRepo;
	}

	//PreAuthorize ist eine Annotation, welche der automatischen Autorisationserkennung dient.
        /**
         * GET-Endpunkt des Erstellungsformulars.
         * @param m model
         * @param form Erstellungsformular
         * @return "EmployeeRegistration"
         */
	@PreAuthorize("hasRole('BOSS')")
	@GetMapping(path = "/createEmployee")
	public String createEmployee(Model m, EmployeeRegistrationForm form) {
		return "EmployeeRegistration";
	}




        /**
         * POST-Endpunkt des Erstellungsformulars
         * @param form Erstellungsformular
         * @param result Spring Fehlerliste
         * @param redirectAttributes Redirect-Model
         * @param model model
         * @return template-name des nächsten Schrittes
         */
	@PreAuthorize("hasRole('BOSS')")
	@PostMapping(path = "/createEmployee")
	String createEmployee(@Valid EmployeeRegistrationForm form, Errors result, RedirectAttributes redirectAttributes, Model model) {
		Short creationResult;

		if (result.hasErrors()) {
			return "EmployeeRegistration";
		}


		creationResult = employeeService.createEmployee(form);


		switch (creationResult) {
			case 0: redirectAttributes.addFlashAttribute("createdUser", "Ein neuer Nutzer wurde erfolgreich angelegt");
				return "redirect:/login";

			case 1:
				model.addAttribute("error", "Leider scheint sich irgendwo ein Fehler eingeschlichen zu Haben. Bitte achten Sie darauf nur ganzzahlige Gehälter zu vergeben.");
				break;
			case 2:
				model.addAttribute("error", "Mitarbeiter existiert bereits. Job-Mail gefunden.");
				break;
			case 3:
				model.addAttribute("error", "Leider ist die E-Mail-Endung fehlerhaft.");
				break;
			case 4:
				model.addAttribute("error", "Unbekannter E-Mail-Provider. Bitte Schreibweise prüfen.");
				break;
			case 5:
				model.addAttribute("error", "Leider ist die Eingabe der Stunden nicht mit dem dem Arbeitszeitschutzgesetz vereinbar.");
				break;
			case 6:
				model.addAttribute("error", "Ihr Mitarbeiter wird zu schlecht bezahlt");
				break;
			case 7:
				model.addAttribute("error", "Leider gab eis einen Fehler beim Einlesen der Stundenzahl.");
				break;
			case 8:
				model.addAttribute("error", "Leider gab es einen Fehler beim erstellen des Gehalts");
				break;
			case 9:
				model.addAttribute("error", "Ein undefinierter Fehler ist aufgetreten.");
				break;
			case 10:
				model.addAttribute("error", "Ihre Postleitzahl scheint Fehler zu enthalten. Entweder ist die Länge oder der Inhalt fehlerhaft.");
				break;
			default:
				model.addAttribute("error", "Leider scheint sich irgendwo ein undefinierter Fehler eingeschlichen zu haben. Bitte achten Sie darauf nur ganzzahlige Gehälter einzugeben und auf jegliche Sonderzeichen zu verzichten, sofern möglich.");
				break;
		}

		model.addAttribute("employeeRegistrationForm", form);
		return "EmployeeRegistration";
	}




        /**
         * GET-Endpunkt: Alle Mitarbeiter Anzeigen
         * @param m model
         * @return "employees"
         */
	@PreAuthorize("hasRole('BOSS')")
	@GetMapping(path = "/staff")
	public String showAllEmployees(Model m) {
		List<EmployeeEntry> employees = employeeRepo.findAll();

		m.addAttribute("employees", employees);
		System.out.println(employees.size());
		return "employees";
	}




        /**
         * GET-Endpunkt: Mitarbeiter bearbeiten.
         * @param id Mitarbeiter-ID
         * @param model MVC-Model
         * @return Template-Name
         */
	@PreAuthorize("hasRole('BOSS')")
	@GetMapping(path = "/editUser")
	String editUser(@RequestParam("id") UUID id, Model model) {
		Optional<EmployeeEntry> employeeOpt = employeeRepo.findByIdIdentifier(id); //	.findAll().stream().toList();
		Optional<UserEntry> userOpt = userRepo.findByIdIdentifier(id); // 		.findAll().stream().toList();


		if (employeeOpt.isPresent() && userOpt.isPresent()) {
			model.addAttribute("employee", employeeOpt.get());
			model.addAttribute("user", userOpt.get());
			model.addAttribute("title", "Edit User - UFO Kinos"); // Setzen Sie den Titel hier
			return "manage-staff";
		} else {
			return "redirect:/errorPage";
		}
	}




        /**
         * POST-Endpunkt: Mitarbeiter bearbeiten
         * @param id id
         * @param firstName Vorname
         * @param lastName Nachname
         * @param email E-Mail
         * @param job Job
         * @param salary Gehalt
         * @param hours Wochenstunden
         * @param redirectAttributes Redirect-Model
         * @return Redirect-Template des nächsten Schritts
         */
	@PreAuthorize("hasRole('BOSS')")
	@PostMapping (path = "/editUser")
	String editUser(@RequestParam("id") UserEntry.UserIdentifier id,
					@RequestParam("firstName") String firstName,
					@RequestParam("lastName") String lastName,
					@RequestParam("email") String email,
					@RequestParam("job") String job,
					@RequestParam("salary") String salary,
					@RequestParam("hours") String hours,
					RedirectAttributes redirectAttributes) {
		int result = employeeService.editEmployee(id, firstName, lastName, email, job, salary, hours);

		switch (result)
		{
			case 0:
				return "redirect:/manage/staff";
			case 1:
				redirectAttributes.addFlashAttribute("error", "Zu wenig Arbeitszeit.");
				break;
			case 2:
				redirectAttributes.addFlashAttribute("error", "Ihr Mitarbeiter überarbeitet sich.");
				break;
			case 3:
				redirectAttributes.addFlashAttribute("error", "Das Gehalt ist zu niedrig.");
				break;
			case 4:
				redirectAttributes.addFlashAttribute("error", "Ein Fehler ist ist bei der Stundeneingabe aufgetreten.");
				break;
			case 5:
				redirectAttributes.addFlashAttribute("error", "Ein Fehler ist ist bei der Gehaltseingabe aufgetreten.");
				break;
		}



		return "redirect:/manage/editUser?id=" + id.getId();
	}
}
