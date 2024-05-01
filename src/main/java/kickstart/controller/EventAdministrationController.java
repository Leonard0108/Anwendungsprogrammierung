package kickstart.controller;

import jakarta.validation.Valid;
import kickstart.models.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EventAdministrationController {
	@GetMapping("/event-administration")
	//@PreAuthorize("hasRole('BOSS')")
	public void getEvents(Model m){}

	@PostMapping("/event-administration")
	//@PreAuthorize("hasRole('BOSS')")
	public void addEvent(@Valid Event eventForm, Errors result){}
}
