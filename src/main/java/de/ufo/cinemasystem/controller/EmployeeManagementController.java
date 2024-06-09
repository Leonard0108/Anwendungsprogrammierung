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


@Controller
@RequestMapping(path = "/EmployeeControlling")
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
	//@PreAuthorize("BOSS")
	@GetMapping(path = "/createEmployee")
	public String createEmployee(Model m, EmployeeRegistrationForm form) {
		return "EmployeeRegistration";
	}



	//@PreAuthorize("BOSS")
	@PostMapping(path = "/createEmployee")
	String createEmployee(@Valid EmployeeRegistrationForm form, Errors result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			System.out.println(result.getAllErrors());
			return "EmployeeRegistration";
		}

		System.out.println(form);

		employeeService.createEmployee(form);
		redirectAttributes.addFlashAttribute("createdUser", "Ein neuer Nutzer wurde erfolgreich angelegt");
		System.out.println("createdUser: " + form);

		return "redirect:/login";
	}




	@GetMapping(path = "/showAllEmployees")
	public String showAllEmployees(Model m) {
		List<EmployeeEntry> employees = employeeRepo.findAll();

		m.addAttribute("employees", employees);
		System.out.println(employees.size());
		return "employees";
	}



	@GetMapping(path = "/editUser")
	String editUser(@RequestParam("id") UUID id, Model model) {
		// Retrieve employee by id and add to model
		Optional<EmployeeEntry> employeeOpt = employeeRepo.findByIdIdentifier(id);
		Optional<UserEntry> userOpt = userRepo.findByIdIdentifier(id);
		if(employeeOpt.isPresent() && userOpt.isPresent()) {
			model.addAttribute("employee", employeeOpt.get());
			model.addAttribute("user", userOpt.get());
			return "manage-staff";
		}else {
			// Handle the case where the employee was not found
			return "redirect:/errorPage"; // Redirect to an error page or another appropriate page
		}
	}




	@PostMapping (path = "/editUser")
	String editUser(@RequestParam("id") UserEntry.UserIdentifier id,
					@RequestParam("name") String firstName,
					@RequestParam("name") String lastName,
					@RequestParam("email") String email,
					@RequestParam("job") String job,
					@RequestParam("salary") String salary,
					@RequestParam("hours") String hours) {
		employeeService.editEmployee(id, firstName, lastName, email, job, salary, hours);


		return "redirect:/";

	}


	/*@PreAuthorize("BOSS")
	@PostMapping(path = "changeRole")
	String changeRole(@RequestParam int employeeId, @RequestParam String role) {
		return "redirect:/";
	}*/
}
