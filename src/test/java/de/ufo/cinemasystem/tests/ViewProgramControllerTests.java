
package de.ufo.cinemasystem.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Jannik Schwaß
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ViewProgramControllerTests {
    
    @Autowired
    MockMvc mvc;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CinemaShowRepository cinemaShowRepository;

	@Autowired
	private CinemaHallRepository cinemaHallRepository;

	@Autowired
	private FilmRepository filmRepository;

	@Autowired
	private AdditionalDateTimeWorker additionalDateTimeWorker;

    /**
     * Check the public ViewProgramController endpoints and that nonpublic ones are correctly locked down.
     * @throws Exception 
     */
    @Test
    void testPublicEndpoints() throws Exception{
        System.out.println("IntegrationTest: ViewProgramController");
        //public endpoints status code checks
        mvc.perform(get("/current-films")) 
                .andExpect(status().is3xxRedirection());
        
        mvc.perform(get("/current-films/2024/15")) 
                .andExpect(status().isOk());
        
        mvc.perform(get("/cinema-shows/1")) 
                .andExpect(status().isOk());
        
        
        //check locked down endpoints unavailability
        
        //commented because of parameter validation
        /*mvc.perform(post("/current-films/2024/15"))
        .andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/cinema-shows/1/edit"))
        .andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/cinema-shows/1/delete"))
        .andExpect(status().is3xxRedirection());*/
        
    }

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testAddNewCinemaShow() throws Exception{
		int year = 2024;
		int week = 30;
		System.out.println("Testing add new CinemaShow in ViewProgramController");

		List<Film> films = filmRepository.findAll().toList();
		assert !films.isEmpty() : "Test requires at least 1 films in the repository";
		Film film = films.get(0);

		List<CinemaHall> cinemaHalls = cinemaHallRepository.findAll().toList();
		assert !films.isEmpty() : "Test requires at least 1 cinema-halls in the repository";
		CinemaHall cinemaHall = cinemaHalls.get(0);

		LocalDateTime addTime = LocalDateTime.of(2024, 8, 1, 20, 15);

		mvc.perform(post("/current-films/{year}/{week}", year, week)
				.param("film", film.getId().toString())
				.param("room", cinemaHall.getId().toString())
				.param("addTime", String.valueOf(addTime)))
			.andExpect(status().is3xxRedirection());
		Streamable<CinemaShow> cinemaShows = cinemaShowRepository.findCinemaShowsOnDay(addTime.toLocalDate());
		for(CinemaShow cinemaShow : cinemaShows) {
			if(cinemaShow.getFilm().equals(film) && cinemaShow.getCinemaHall().equals(cinemaHall) &&
			   cinemaShow.getStartDateTime().equals(addTime)) {
				return;
			}
		}
		assert false;
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testCinemaShowEdit() throws Exception{
		System.out.println("Testing edit CinemaShow in ViewProgramController");

		Optional<Film> film2Opt = filmRepository.findById(2L);
		assert film2Opt.isPresent() : "Test requires film with id = 2";
		Film film2 = film2Opt.get();

		Optional<Film> film1Opt = filmRepository.findById(1L);
		assert film1Opt.isPresent() : "Test requires film with id = 1";
		Film film1 = film1Opt.get();

		Streamable<CinemaShow> cinemaShows = cinemaShowRepository.findAllByFilm(film2);
		assert !cinemaShows.isEmpty() : "Test requires cinema-show with film id = 2";
		CinemaShow cinemaShow = cinemaShows.get().findAny().get();

		LocalDateTime editTime = LocalDateTime.of(2024, 8, 3, 16, 45);

		mvc.perform(post("/cinema-shows/{id}/edit", cinemaShow.getId())
				.param("film", film1.getId().toString())
				.param("editTime", String.valueOf(editTime)))
			.andExpect(status().is3xxRedirection());

		Optional<CinemaShow> editCinemaShowOpt = cinemaShowRepository.findById(cinemaShow.getId());
		assert editCinemaShowOpt.isPresent() : "Edit CinemaShow is not longer exsistent!";
		CinemaShow editCinemaShow = editCinemaShowOpt.get();

		assert editCinemaShow.getFilm().equals(film1) && editCinemaShow.getStartDateTime().equals(editTime);
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testCinemaShowDelete() throws Exception{
		System.out.println("Testing delete CinemaShow in ViewProgramController");

		Optional<Film> film2Opt = filmRepository.findById(2L);
		assert film2Opt.isPresent() : "Test requires film with id = 2";
		Film film2 = film2Opt.get();

		Streamable<CinemaShow> cinemaShows = cinemaShowRepository.findAllByFilm(film2);
		assert !cinemaShows.isEmpty() : "Test requires cinema-show with film id = 2";
		CinemaShow cinemaShow = cinemaShows.get().findAny().get();

		mvc.perform(post("/cinema-shows/{id}/delete", cinemaShow.getId()))
			.andExpect(status().is3xxRedirection());

		Optional<CinemaShow> editCinemaShowOpt = cinemaShowRepository.findById(cinemaShow.getId());
		assert editCinemaShowOpt.isEmpty();
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	public void testGetCurrentProgram() throws Exception {
		int year = 2024;
		int week = 30;
		LocalDate startDate = LocalDate.of(2024, 7, 22);  // Beispiel: 22. Juli 2024 als Startdatum der Woche

		mockMvc.perform(get("/current-films/{year}/{week}", year, week))
			.andExpect(status().isOk())
			.andExpect(view().name("current-films"))
			.andExpect(model().attributeExists("oneWeekCinemaShows"))
			.andExpect(model().attributeExists("allCinemaHalls"))
			.andExpect(model().attributeExists("allFilms"))
			.andExpect(model().attributeExists("lastWeekRangeFormat"))
			.andExpect(model().attributeExists("currentWeekRangeFormat"))
			.andExpect(model().attributeExists("nextWeekRangeFormat"))
			.andExpect(model().attributeExists("lastYear"))
			.andExpect(model().attributeExists("lastWeek"))
			.andExpect(model().attributeExists("nextYear"))
			.andExpect(model().attributeExists("nextWeek"))
			.andExpect(model().attribute("title", "Filmplan"));
	}
}
