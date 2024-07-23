package de.ufo.cinemasystem.tests;

import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.services.SnacksService;
import org.junit.jupiter.api.Test;
import org.salespointframework.catalog.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ManageStorageControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	SnacksService snacksService;

	@Autowired
	SnacksRepository snacksRepository;

	// =====================================================================================================================
	// 5 Tests: Prüfe, ob Endpunkt (/manage/storage) nur für Nutzer mit den Rollen AUTHORIZED_EMPLOYEE und BOSS zugänglich ist
	// und ob ein nicht nicht angemeldeter Nutzer umgeleitet wird
	// =====================================================================================================================

	@Test
	void testGetPageNotAuthorized() throws Exception {
		System.out.println("Testing ManageStorageController NotAuthorized");
		mvc.perform(get("/manage/storage"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	@Transactional
	void testGetPageUserUnauthorized() throws Exception {
		System.out.println("Testing ManageStorageController Unauthorized with role USER");
		mvc.perform(get("/manage/storage"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "em", roles = "EMPLOYEE")
	@Transactional
	void testGetPageEmployeeUnauthorized() throws Exception {
		System.out.println("Testing ManageStorageController Unauthorized with role EMPLOYEE");
		mvc.perform(get("/manage/storage"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testGetPageAuthorizedEmployeeAuthorized() throws Exception {
		System.out.println("Testing ManageStorageController Unauthorized with role AUTHORIZED_EMPLOYEE");
		mvc.perform(get("/manage/storage"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetPageBossAuthorized() throws Exception {
		System.out.println("Testing ManageStorageController with role BOSS");
		mvc.perform(get("/manage/storage"))
			.andExpect(status().isOk());
	}

	// ======================================================================================================

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testSaveInventoryItemCounts() throws Exception{
		System.out.println("Testing ManageStorageController saveItems()");

		List<Snacks> snacks = snacksRepository.findAll(Sort.sort(Snacks.class).by(Snacks::getId).descending()).toList();
		assert snacks.size() >= 5 : "Test requires at least 5 snacks in the repository";

		List<Integer> snackCounts = List.of(10, 20, 30, 40, 50);

		String snackObjectsParam = snacks.subList(0, 5).stream()
			.map(Product::getId).filter(Objects::nonNull)
			.map(Product.ProductIdentifier::toString)
			.collect(Collectors.joining(","));
		String snackCountsParam = snackCounts.stream()
			.map(String::valueOf)
			.collect(Collectors.joining(","));

		mvc.perform(post("/manage/storage/save")
				.param("snack-objects", snackObjectsParam)
				.param("snack-counters", snackCountsParam))
			.andExpect(status().is3xxRedirection());

		// Prüft, ob die 5 ersten Snacks, sortiert nach ID auch wirklich (10, 20, 30, 40, 50) Items besitzen
		assert snacksRepository.findAll(Sort.sort(Snacks.class).by(Snacks::getId).descending()).toList().subList(0, 5).stream()
			.map(s -> snacksService.getStock(s.getId())).toList()
			.equals(snackCounts);
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testSaveInventoryNegativeItemCount() throws Exception{
		System.out.println("Testing ManageStorageController saveItems()");

		List<Snacks> snacks = snacksRepository.findAll(Sort.sort(Snacks.class).by(Snacks::getId).descending()).toList();
		assert !snacks.isEmpty() : "Test requires at least 1 snack in the repository";

		final int snackCount = -5;

		String snackObjectParam = Objects.requireNonNull(snacks.get(0).getId()).toString();

		mvc.perform(post("/manage/storage/save")
				.param("snack-objects", snackObjectParam)
				.param("snack-counters", String.valueOf(snackCount)))
			.andExpect(status().is3xxRedirection());

		// Prüft, ob bei einem negativen eingegebenen Wert, 0 Items gesetzt werden
		assert snacksService.getStock(
			snacksRepository.findAll(Sort.sort(Snacks.class).by(Snacks::getId).descending()).toList().get(0).getId()
		) == 0;
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testCreateNewItemWithoutImage() throws Exception{
		System.out.println("Testing ManageStorageController newItem()");

		final String snackName = "salziges Popcorn (jumbo big)";
		final Snacks.SnackType snackType = Snacks.SnackType.Essen;

		mvc.perform(multipart("/manage/storage/item/new")
				.param("whatNew", snackName)
				.param("itemType", snackType.toString()))
			.andExpect(status().is3xxRedirection());

		Optional<Snacks> snackOpt = snacksRepository.findByName(snackName).stream().findAny();
		// Prüft, ob Snack in DB gespeichert wurde
		assert snackOpt.isPresent();

		Snacks snack = snackOpt.get();

		// Prüft, ob Name, SnackType richtig gesetzt wurden und kein Bild existiert
		assert (snack.getName().equals(snackName) && snack.getSnackType().equals(snackType.toString()) && snack.getImageBase64() == null);
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testCreateNewItemWithImage() throws Exception{
		System.out.println("Testing ManageStorageController newItem()");

		final String snackName = "salziges Popcorn (jumbo big)";
		final Snacks.SnackType snackType = Snacks.SnackType.Essen;
		MockMultipartFile snackImage = new MockMultipartFile(
			"file",
			"salt-popcorn.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			loadImageAsByteArray("salt-popcorn.png")
		);

		mvc.perform(multipart("/manage/storage/item/new")
				.file("imageFile", snackImage.getBytes())
				.param("whatNew", snackName)
				.param("itemType", snackType.toString()))
			.andExpect(status().is3xxRedirection());

		Optional<Snacks> snackOpt = snacksRepository.findByName(snackName).stream().findAny();
		// Prüft, ob Snack in DB gespeichert wurde
		assert snackOpt.isPresent();

		Snacks snack = snackOpt.get();

		// Prüft, ob Name, SnackType richtig gesetzt wurden und kein Bild existiert
		assert (snack.getName().equals(snackName) && snack.getSnackType().equals(snackType.toString()) &&
			snack.getImageBase64().equals(Base64.getEncoder().encodeToString(snackImage.getBytes())));
	}

	private byte[] loadImageAsByteArray(String filename) throws IOException {
		ClassPathResource imgFile = new ClassPathResource("static/assets/snacks/" + filename);
		return Files.readAllBytes(imgFile.getFile().toPath());
	}
}
