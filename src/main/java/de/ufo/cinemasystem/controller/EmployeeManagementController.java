package de.ufo.cinemasystem.controller;


import de.ufo.cinemasystem.additionalfiles.RegistrationForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller()
@RequestMapping(path = "/EmployeeControlling")
public class EmployeeManagementController {
	@PreAuthorize("BOSS")
	@GetMapping(path = "/createEmployee")
	public String createEmployee() {
		return "EmployeeControlling";
	}




	@PreAuthorize("BOSS")
	@PostMapping(path = "/createEmployee")
	public String createEmployee(RegistrationForm registrationForm)
	{
		return "redirect:/";
	}




	@PreAuthorize("BOSS")
	@PostMapping(path = "changeRole")
	public String changeRole(@RequestParam int employeeId, @RequestParam String role) {
		return "redirect:/";
	}
}
