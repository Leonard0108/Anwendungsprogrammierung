package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.repository.SnacksRepository;
import org.salespointframework.catalog.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManageStorageController {

	private SnacksRepository snacksRepository;

	ManageStorageController(SnacksRepository snacksRepository) {
		this.snacksRepository = snacksRepository;
	}

	@GetMapping("/manage-storage/")
	public String showStorage(Model model) {
		model.addAttribute("allSnacks", snacksRepository.findAll());

		return "manage-storage";
	}
}
