package de.ufo.cinemasystem.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.services.SnacksService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import javax.imageio.ImageReader;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Spring MVC-Controller der Lagerverwaltung
 * @author Yannick Harnisch
 * @author Jannik Schwaß
 */
@Controller
public class ManageStorageController {

	private SnacksRepository snacksRepository;
	private SnacksService snacksService;

        /**
         * Erstelle einen neuen Controller mit den angegebenen Abhängigkeiten.
         * @param snacksRepository Implementierung Snack-Repository
         * @param snacksService Snack-Service
         */
	ManageStorageController(SnacksRepository snacksRepository, SnacksService snacksService) {
		this.snacksRepository = snacksRepository;
		this.snacksService = snacksService;
	}

        /**
         * GET-Endpunkt: Lagerbestand anzeigen
         * @param model Modell
         * @return "manage_storage"
         */
	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@GetMapping("/manage/storage")
	public String showStorage(Model model) {
		LinkedHashMap<Snacks, Integer> allSnacks = snacksRepository.findAll(Sort.sort(Snacks.class).by(Snacks::getName).ascending()).stream()
				.collect(Collectors.toMap(s -> s, s -> snacksService.getStock(s.getId()), (o, n) -> o, LinkedHashMap::new));

		model.addAttribute("allSnacks", allSnacks);
		// TODO: SnackType auslagern und statisch ueber Thymeleaf aufrufen
		model.addAttribute("snackTypes", Snacks.SnackType.values());
                model.addAttribute("title", "Lagerverwaltung");

		return "manage_storage";
	}

        /**
         * POST-Endpunkt: Neues Item anlegen
         * @param newSnack Snack-Name
         * @param snackType Snack-Typ
         * @param file Bild (optional)
         * @param redirectAttributes Redirect-Modell
         * @return "redirect:/manage/storage"
         */
	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/manage/storage/item/new")
	public String newItem(@RequestParam("whatNew") String newSnack, @RequestParam("itemType") Snacks.SnackType snackType,
						  @RequestPart(value = "imageFile", required = false) MultipartFile file, RedirectAttributes redirectAttributes) {
		if(snacksRepository.findAll().stream().anyMatch(e -> e.getName().equalsIgnoreCase(newSnack))) {
			redirectAttributes.addFlashAttribute("errorMessageNew", "Item bereits vorhanden!");
			return "redirect:/manage/storage";
		}
                if(file != null && !file.isEmpty()){
					String ctype = file.getContentType();
					if(ctype == null) {
						redirectAttributes.addFlashAttribute("errorMessageNew", "Nicht unterstüztes Dateiformat!");
						return "redirect:/manage/storage";
					}
					//if((!ctype.startsWith("images/"))) {
					//	redirectAttributes.addFlashAttribute("errorMessageNew", "Nicht unterstüztes Dateiformat!");
					//	return "redirect:/manage/storage";
					//}
                }
                
                if(newSnack == null || newSnack.isBlank()){
                    redirectAttributes.addFlashAttribute("errorMessageNew", "Bitte vergeben Sie einen Namen!");
                    return "redirect:/manage/storage";
                }
                
		if(file != null && !file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
                                //try reading it first, in case someone attempts to manipulate the content type header.
                                boolean ok = false;
                                boolean wasCached = javax.imageio.ImageIO.getUseCache();
                                javax.imageio.ImageIO.setUseCache(false);
                                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                                try {
                                    BufferedImage read = javax.imageio.ImageIO.read(in);
                                    ok = read != null;
                                    
                                } catch (IOException ex) {
                                    System.getLogger(ManageStorageController.class.getName()).log(System.Logger.Level.INFO, "Bild konnte nicht geladen werden!", ex);
                               }
                                /*Iterator<ImageReader> imageReadersByMIMEType = javax.imageio.ImageIO.getImageReadersByMIMEType(ctype);
                                if(!imageReadersByMIMEType.hasNext()){
                                redirectAttributes.addFlashAttribute("errorMessageNew", "Nicht unterstüztes Dateiformat!");
                                return "redirect:/manage/storage";
                                }
                                while(imageReadersByMIMEType.hasNext()){
                                ImageReader next = imageReadersByMIMEType.next();
                                try {
                                next.re
                                } catch (IOException ex) {
                                System.getLogger(ManageStorageController.class.getName()).log(System.Logger.Level.INFO, "Bild konnte nicht mittels " + next.getClass().getName() + " geladen werden!", ex);
                                }
                                
                                }*/
                                if(wasCached){
                                    javax.imageio.ImageIO.setUseCache(true);
                                }
                                if(!ok){
                                    redirectAttributes.addFlashAttribute("errorMessageNew", "Bild nicht lesbar oder nicht unterstützter Dateityp!");
                                    return "redirect:/manage/storage";
                                }
                                
				snacksService.createSnack(newSnack, Money.of(9.99, "EUR"), snackType, 0, bytes);
			} catch (IOException e) {
				throw new InternalError("Backing store failed", e);
			}
		}else {
			snacksService.createSnack(newSnack, Money.of(9.99, "EUR"), snackType, 0);
		}

		// TODO: Behandlung von SnackType und Money (einfügen?)
		redirectAttributes.addFlashAttribute("successMessageNew", "Neues Item erfolgreich angelegt!");

		return "redirect:/manage/storage";
	}

        /**
         * POST-Endpunkt: Lagerbestände aktualisieren
         * @param snackIds Snack Item-IDs
         * @param snackCounters neue Bestände
         * @param redirectAttributes Redirect-Modell
         * @return "redirect:/manage/storage"
         */
	@PreAuthorize("hasAnyRole('BOSS', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/manage/storage/save")
	public String saveItems(@RequestParam("snack-objects") List<Product.ProductIdentifier> snackIds, @RequestParam("snack-counters") List<Integer> snackCounters, RedirectAttributes redirectAttributes) {
		final int count = snackIds.size();
		boolean error = false;
		for(int i = 0; i < count; i++) {
			try {
				this.snacksService.setStock(snackIds.get(i), snackCounters.get(i) > 0 ? snackCounters.get(i) : 0);
			}catch (IllegalArgumentException ex) {
				error = true;
			}
		}

		if(error) {
			redirectAttributes.addFlashAttribute("errorMessageSave", "Fehler beim aktualisieren eines oder mehrerer Items!");
		}else {
			redirectAttributes.addFlashAttribute("successMessageSave", "Items wurden erfolgreich aktualisiert!");
		}

		return "redirect:/manage/storage";
	}
}