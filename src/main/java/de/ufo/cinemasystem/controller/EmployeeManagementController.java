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


@Controller
@RequestMapping(path = "/manage")
public class EmployeeManagementController {
	EmployeeRepository employeeRepo;

	UserRepository userRepo;
	EmployeeService employeeService;


	EmployeeManagementController(EmployeeService employeeService, EmployeeRepository employeeRepo, UserRepository userRepo) {
		this.employeeService = employeeService;
		this.employeeRepo = employeeRepo;
		this.userRepo = userRepo;
	}

	//PreAuthorize ist eine Annotation, welche der automatischen Autorisationserkennung dient.
	@PreAuthorize("hasRole('BOSS')")
	@GetMapping(path = "/createEmployee")
	public String createEmployee(Model m, EmployeeRegistrationForm form) {
		return "EmployeeRegistration";
	}

	@PreAuthorize("hasRole('BOSS')")
	@PostMapping(path = "/createEmployee")
	String createEmployee(@Valid EmployeeRegistrationForm form, Errors result, RedirectAttributes redirectAttributes) {
		short creationResult;

		if (result.hasErrors()) {
			//System.out.println(result.getAllErrors());
			return "EmployeeRegistration";
		}

		//System.out.println(form);

		creationResult = employeeService.createEmployee(form);


		switch (creationResult) {
			case 0: redirectAttributes.addFlashAttribute("createdUser", "Ein neuer Nutzer wurde erfolgreich angelegt");
				return "redirect:/login";
			case 1:
				redirectAttributes.addFlashAttribute("error", "Mitarbeiter existiert bereits: Job-Mail gefunden.");
				break;
			case 2:
				redirectAttributes.addFlashAttribute("error", "Leider ist die E-Mail-Endung fehlerhaft.");
				break;
			case 3: redirectAttributes.addFlashAttribute("error", "Ihr Mitarbeiter überarbeitet sich oder wird zu schlecht bezahlt.");
				break;
			case 4:
				redirectAttributes.addFlashAttribute("error", "Unbekannter E-Mail-Provider. Bitte Schreibweise prüfen.");
		}

		//System.out.println("createdUser: " + form);

		return "redirect:/manage/staff";
	}




	@PreAuthorize("hasRole('BOSS')")
	@GetMapping(path = "/staff")
	public String showAllEmployees(Model m) {
		List<EmployeeEntry> employees = employeeRepo.findAll();

		m.addAttribute("employees", employees);
		System.out.println(employees.size());
		return "employees";
	}



	/*@PostMapping(path = "/showAllEmployees")
	public String showAllEmployeesRedirect(Model m) {
		return "redirect::/EmployeeControlling/showAllEmployees";
	}*/




	@PreAuthorize("hasRole('BOSS')")
	@GetMapping(path = "/editUser")
	String editUser(@RequestParam("id") UUID id, Model model) {
		Optional<EmployeeEntry> employeeOpt = employeeRepo.findByIdIdentifier(id); //	.findAll().stream().toList();
		Optional<UserEntry> userOpt = userRepo.findByIdIdentifier(id); // 		.findAll().stream().toList();
		/*
		for(EmployeeEntry employee : employeeOpt){
			System.out.println("employee: " + employee.getId().getId());
		}
		for(UserEntry user : userOpt){
			System.out.println("user: " + user.getId().getId());
			System.out.println(user.getFirstName());
		}
		*/

		if (employeeOpt.isPresent() && userOpt.isPresent()) {
			model.addAttribute("employee", employeeOpt.get());
			model.addAttribute("user", userOpt.get());
			model.addAttribute("title", "Edit User - UFO Kinos"); // Setzen Sie den Titel hier
			return "manage-staff";
		} else {
			return "redirect:/errorPage";
		}
	}




	@PreAuthorize("hasRole('BOSS')")
	@PostMapping (path = "/editUser")
	String editUser(@RequestParam("id") UserEntry.UserIdentifier id,
					@RequestParam("firstName") String firstName,
					@RequestParam("lastName") String lastName,
					@RequestParam("email") String email,
					@RequestParam("job") String job,
					@RequestParam("salary") String salary,
					@RequestParam("hours") String hours) {
		employeeService.editEmployee(id, firstName, lastName, email, job, salary, hours);

		return "redirect:/manage/staff";
	}


	/*@PreAuthorize("BOSS")
	@PostMapping(path = "changeRole")
	String changeRole(@RequestParam int employeeId, @RequestParam String role) {
		return "redirect:/";
	}*/
}
