package kickstart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RentFilmController {

	@GetMapping("/rent-films/")
	public String getRentFilms() {
		return "films-rental-renderer";
	}
}
