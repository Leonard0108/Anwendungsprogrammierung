package kickstart.controller;

import jakarta.validation.Valid;
import kickstart.models.Event;
import kickstart.repository.EventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EventAdministrationController {

	private EventRepository eventRepository;

	public EventAdministrationController(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}


	@GetMapping("/event-administration")
	//@PreAuthorize("hasRole('BOSS')")
	public String getEvents(Model m){

		return "manage-rooms-boss-renderer";
	}

	@PostMapping("/event-administration")
	//@PreAuthorize("hasRole('BOSS')")
	public String addEvent(@Valid Event eventForm, Errors result){

		return "manage-rooms-boss-renderer";
	}
}
