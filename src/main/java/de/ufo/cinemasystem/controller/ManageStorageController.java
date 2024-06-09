package de.ufo.cinemasystem.controller;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.javamoney.moneta.Money;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.services.SnacksService;

@Controller
public class ManageStorageController {

	private SnacksRepository snacksRepository;
	private SnacksService snacksService;

	ManageStorageController(SnacksRepository snacksRepository, SnacksService snacksService) {
		this.snacksRepository = snacksRepository;
		this.snacksService = snacksService;
	}

	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@GetMapping("/manage/storage")
	public String showStorage(Model model) {
		LinkedHashMap<Snacks, Integer> allSnacks = snacksRepository.findAll(Sort.sort(Snacks.class).by(Snacks::getName).ascending()).stream()
				.collect(Collectors.toMap(s -> s, s -> snacksService.getStock(s.getId()), (o, n) -> o, LinkedHashMap::new));

		model.addAttribute("allSnacks", allSnacks);
		// TODO: SnackType auslagern und statisch ueber Thymeleaf aufrufen
		model.addAttribute("snackTypes", Snacks.SnackType.values());
                model.addAttribute("title", "Lagerverwaltung");

		return "manage-storage";
	}

	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/manage/storage/item/new")
	public String newItem(@RequestParam("whatNew") String newSnack, @RequestParam("itemType") Snacks.SnackType snackType, RedirectAttributes redirectAttributes) {
		if(snacksRepository.findAll().stream().anyMatch(e -> e.getName().equalsIgnoreCase(newSnack))) {
			redirectAttributes.addFlashAttribute("errorMessageNew", "Item bereits vorhanden!");
			return "redirect:/manage/storage";
		}
		// TODO: Behandlung von SnackType und Money (einfügen?)
		snacksService.createSnack(newSnack, Money.of(9.99, "EUR"), snackType, 0);
		redirectAttributes.addFlashAttribute("successMessageNew", "Neues Item erfolgreich angelegt!");

		return "redirect:/manage/storage";
	}

	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/manage/storage/item/add")
	public String addItem(@RequestParam("itemName") String snackId, @RequestParam("itemCount") int itemCount, RedirectAttributes redirectAttributes) {
		try {
			this.snacksService.addStock(snackId, itemCount);
			redirectAttributes.addFlashAttribute("successMessageAdd", itemCount + " Items erfolgreich hinzugefügt!");
		}catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessageAdd", "Ungültige hinzuzufügende Item-Anzahl!");
		}

		return "redirect:/manage/storage";
	}

	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/manage/storage/item/remove")
	public String removeItem(@RequestParam("itemName") String snackId, @RequestParam("itemCount") int itemCount, RedirectAttributes redirectAttributes) {
		try {
			this.snacksService.removeStock(snackId, itemCount);
			redirectAttributes.addFlashAttribute("successMessageRemove", itemCount + " Items erfolgreich entfernt!");
		}catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessageRemove", "Ungültige abzuziehende Item-Anzahl!");
			redirectAttributes.addFlashAttribute("infoMessageRemove", "Es können max. so viele Items abgezogen werden, wie sich im Lager befinden!");
		}

		return "redirect:/manage/storage";
	}
}