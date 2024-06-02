package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.services.SnacksService;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class ManageStorageController {

	private SnacksRepository snacksRepository;
	private SnacksService snacksService;

	ManageStorageController(SnacksRepository snacksRepository, SnacksService snacksService) {
		this.snacksRepository = snacksRepository;
		this.snacksService = snacksService;
	}

	@GetMapping("/manage/storage")
	public String showStorage(Model model) {
		Map<Snacks, Integer> allSnacks = snacksRepository.findAll().stream()
				.collect(Collectors.toMap(s -> s, s -> snacksService.getStock(s.getId())));

		model.addAttribute("allSnacks", allSnacks);
		// TODO: SnackType auslagern und statisch ueber Thymeleaf aufrufen
		model.addAttribute("snackTypes", Snacks.SnackType.values());

		return "manage-storage";
	}

	@PostMapping("/manage/storage/item/new")
	public String newItem(@RequestParam("whatNew") String newSnack, @RequestParam("itemType") Snacks.SnackType snackType, Model m) {
		if(snacksRepository.findAll().stream().anyMatch(e -> e.getName().equalsIgnoreCase(newSnack))) {
			// TODO Verhalten, wenn Item bereits vorhanden
			return "redirect:/manage/storage/";
		}
		// TODO: Behandlung von SnackType und Money (einfügen?)
		snacksService.createSnack(newSnack, Money.of(9.99, "EUR"), snackType, 0);

		return "redirect:/manage/storage";
	}

	@PostMapping("/manage/storage/item/add")
	public String addItem(@RequestParam("itemName") String snackId, @RequestParam("itemCount") int itemCount, Model m) {
		this.snacksService.addStock(snackId, itemCount);

		return "redirect:/manage/storage";
	}

	@PostMapping("/manage/storage/item/remove")
	public String removeItem(@RequestParam("itemName") String snackId, @RequestParam("itemCount") int itemCount, Model m) {
		this.snacksService.removeStock(snackId, itemCount);

		return "redirect:/manage/storage";
	}
}
