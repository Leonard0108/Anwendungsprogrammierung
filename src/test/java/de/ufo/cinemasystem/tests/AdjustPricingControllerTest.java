package de.ufo.cinemasystem.tests;

import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.repository.FilmRepository;
import de.ufo.cinemasystem.repository.SnacksRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class AdjustPricingControllerTest {
	@Autowired
	MockMvc mvc;

	@Autowired
	FilmRepository filmRepository;

	@Autowired
	SnacksRepository snacksRepository;

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetPage() throws Exception {
		System.out.println("Testing AdjustPricingController");
		mvc.perform(get("/manage/pricing"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testGetPageUnauthorized() throws Exception {
		System.out.println("Testing AdjustPricingController Unauthorized");
		mvc.perform(get("/manage/pricing"))
			.andExpect(status().isForbidden());
	}

	@Test
	void testGetPageNotAuthorized() throws Exception {
		System.out.println("Testing AdjustPricingController NotAuthorized");
		mvc.perform(get("/manage/pricing"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetPriceOfFilm() throws Exception {
		System.out.println("Testing AdjustPricingController GetPriceOf() film");
		assert filmRepository.findAll().toList().size() >= 6 : "Test requires at least 6 films in the repository";
		mvc.perform(get("/manage/pricing")
				.param("ChangePriceOf","film-6"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Film 5")));
	}
	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetPriceOfFilmCorrectPrice() throws Exception {
		System.out.println("Testing AdjustPricingController GetPriceOf() film correct price");

		List<Film> films = filmRepository.findAll().toList();
		assert films.size() >= 6 : "Test requires at least 6 films in the repository";
		Film film = films.get(5);

		mvc.perform(get("/manage/pricing")
				.param("ChangePriceOf","film-" + film.getId()))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(film.getPrice().toString())));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetPriceOfSnack() throws Exception {
		System.out.println("Testing AdjustPricingController GetPriceOf() snack");

		List<Snacks> snacks = snacksRepository.findAll().toList();
		assert snacks.size() >= 6 : "Test requires at least 6 films in the repository";
		Snacks snack = snacks.get(5);

		mvc.perform(get("/manage/pricing")
				.param("ChangePriceOf","snack-" + snack.getId()))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(snack.getName())));
	}
	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetPriceOfSnackCorrectPrice() throws Exception {
		System.out.println("Testing AdjustPricingController GetPriceOf() snack correct price");

		List<Snacks> snacks = snacksRepository.findAll().toList();
		assert snacks.size() >= 6 : "Test requires at least 6 films in the repository";
		Snacks snack = snacks.get(5);

		mvc.perform(get("/manage/pricing")
				.param("ChangePriceOf","snack-" + snack.getId()))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(snack.getPrice().toString())));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testSetPriceOfFilm() throws Exception{
		System.out.println("Testing AdjustPricingController SetPriceOf() film");

		List<Film> films = filmRepository.findAll().toList();
		assert films.size() >= 6 : "Test requires at least 6 films in the repository";
		Film film = films.get(5);

		mvc.perform(post("/manage/pricing")
				.param("selectedId", "film-" + film.getId())
				.param("newPrice", "15.0"))
			.andExpect(status().is3xxRedirection());
			assert Objects.equals(filmRepository.findAll().toList().get(5).getPrice(), Money.of(15.0, "EUR"));
	}
	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testSetPriceOfSnack() throws Exception{
		System.out.println("Testing AdjustPricingController SetPriceOf() snack");

		List<Snacks> snacks = snacksRepository.findAll().toList();
		assert snacks.size() >= 6 : "Test requires at least 6 films in the repository";
		Snacks snack = snacks.get(5);

		mvc.perform(post("/manage/pricing")
				.param("selectedId", "snack-" + snack.getId())
				.param("newPrice", "2.55"))
			.andExpect(status().is3xxRedirection());
		assert Objects.equals(snacksRepository.findAll().toList().get(5).getPrice(), Money.of(2.55, "EUR"));
	}
}
