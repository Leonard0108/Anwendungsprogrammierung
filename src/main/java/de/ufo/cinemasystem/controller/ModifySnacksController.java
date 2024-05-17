package de.ufo.cinemasystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModifySnacksController {

	@GetMapping("/sell/")
	public String () {
		return "films-rental-renderer";
	}
}
