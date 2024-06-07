package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.repository.FilmRepository;
import de.ufo.cinemasystem.repository.SnacksRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdjustPricingController {
	private FilmRepository filmRepository;
	private SnacksRepository snacksRepository;

	public AdjustPricingController(FilmRepository filmRepository, SnacksRepository snacksRepository) {
		this.filmRepository = filmRepository;
		this.snacksRepository = snacksRepository;
	}

	@GetMapping("/manage/pricing")
	@PreAuthorize("hasRole('BOSS')")
	String getAdjustPricing(Model m){
		List<Film> allFilms = filmRepository.findAll().toList();
		List<Snacks> allSnacks = snacksRepository.findAll().toList();
		m.addAttribute("allFilms", allFilms);
		m.addAttribute("allSnacks", allSnacks);
		m.addAttribute("title", "Preisgestaltung");

		return "adjust-pricing";
	}

	@GetMapping(value = "/manage/pricing", params = "ChangePriceOf")
	@PreAuthorize("hasRole('BOSS')")
	String getPriceOf(Model m, @RequestParam("ChangePriceOf") String changePriceOf){
		List<Film> allFilms = filmRepository.findAll().toList();
		List<Snacks> allSnacks = snacksRepository.findAll().toList();
		m.addAttribute("title", "Preisgestaltung");
		m.addAttribute("allFilms", allFilms);
		m.addAttribute("allSnacks", allSnacks);

		String[] parts = changePriceOf.split("-", 2);
		String type = parts[0]; // "film" oder "snack"
		if(type.equals("film")){
			Long id = Long.parseLong(parts[1]);
			m.addAttribute("selected", filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid film ID: " + id)));
		}else if(type.equals("snack")){
			Product.ProductIdentifier id = Product.ProductIdentifier.of(parts[1]);
			m.addAttribute("selected", snacksRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid snack ID: " + id)));
		}else{
			System.out.println("Id des ausgewählten Objekts ist weder Film noch Snack");
		}
		return "adjust-pricing";
	}

	@PostMapping("/manage/pricing")
	@PreAuthorize("hasRole('BOSS')")
	String setPriceOf(RedirectAttributes m, @RequestParam("selectedId") String selectedId, @RequestParam("newPrice") double newPrice){
		List<Film> allFilms = filmRepository.findAll().toList();
		List<Snacks> allSnacks = snacksRepository.findAll().toList();
		m.addFlashAttribute("title", "Preisgestaltung");
		m.addFlashAttribute("allFilms", allFilms);
		m.addFlashAttribute("allSnacks", allSnacks);

		String[] parts = selectedId.split("-", 2);
		String type = parts[0]; // "film" oder "snack"
		if(type.equals("film")) {
			Long id = Long.parseLong(parts[1]);
			Film filmToChangePriceOf = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid film ID: " + id));
			filmToChangePriceOf.setPrice(Money.of(newPrice, "EUR"));
			filmRepository.save(filmToChangePriceOf);
		}else if(type.equals("snack")) {
			Product.ProductIdentifier id = Product.ProductIdentifier.of(parts[1]);
			Snacks snackToChangePriceOf = snacksRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid snack ID: " + id));
			snackToChangePriceOf.setPrice(Money.of(newPrice, "EUR"));
			snacksRepository.save(snackToChangePriceOf);
		}else{
			System.out.println("Id des ausgewählten Objekts ist weder Film noch Snack");
		}




		return "redirect:/manage/pricing?ChangePriceOf=" + selectedId;
	}


}
