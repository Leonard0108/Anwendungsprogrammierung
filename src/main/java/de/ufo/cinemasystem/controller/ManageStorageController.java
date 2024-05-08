package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.repository.SnacksRepository;
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
import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class ManageStorageController {

	private SnacksRepository snacksRepository;

	ManageStorageController(SnacksRepository snacksRepository) {
		this.snacksRepository = snacksRepository;
	}

	@GetMapping("/manage/storage/")
	public String showStorage(Model model) {
		model.addAttribute("allSnacks", snacksRepository.findAll());
		// TODO: SnackType auslagern und statisch ueber Thymeleaf aufrufen
		model.addAttribute("snackTypes", Snacks.SnackType.values());

		return "manage-storage";
	}

	@PostMapping("/manage/storage/item/new")
	public String newItem(@RequestParam("whatNew") String newSnack, @RequestParam("itemType") Snacks.SnackType snackType, Model m) {
		if(snacksRepository.existsByName(newSnack)) {
			// TODO Verhalten, wenn Item bereits vorhanden
			return "redirect:/manage/storage/";
		}
		// TODO: Behandlung von SnackType und Money (einfügen?)
		Snacks snack = new Snacks(newSnack, snackType, Money.of(10.00, EURO), 0);
		this.snacksRepository.save(snack);

		return "redirect:/manage/storage/";
	}

	@PostMapping("/manage/storage/item/add")
	public String addItem(@RequestParam("itemName") Long snackID, @RequestParam("itemCount") int itemCount, Model m) {
		Optional<Snacks> optSnack = snacksRepository.findById(snackID);
		if(optSnack.isEmpty()) {
			// TODO Fehlerbehandlung
			return "redirect:/manage/storage/";
		}
		Snacks snack = optSnack.get();
		snack.addStock(itemCount);

		this.snacksRepository.save(snack);

		return "redirect:/manage/storage/";
	}

	@PostMapping("/manage/storage/item/remove")
	public String removeItem(@RequestParam("itemName") Long snackID, @RequestParam("itemCount") int itemCount, Model m) {
		Optional<Snacks> optSnack = snacksRepository.findById(snackID);
		if(optSnack.isEmpty()) {
			// TODO Fehlerbehandlung
			return "redirect:/manage/storage/";
		}
		Snacks snack = optSnack.get();
		snack.removeStock(itemCount);

		this.snacksRepository.save(snack);

		return "redirect:/manage/storage/";
	}
}
