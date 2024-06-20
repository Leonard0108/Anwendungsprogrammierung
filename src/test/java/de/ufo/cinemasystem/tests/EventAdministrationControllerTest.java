package de.ufo.cinemasystem.tests;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.ufo.cinemasystem.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
public class EventAdministrationControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	EventRepository eventRepository;

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetPage() throws Exception {
		System.out.println("Testing EventAdministrationController");
		mvc.perform(get("/manage/rooms"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testGetPageUnauthorized() throws Exception {
		System.out.println("Testing EventAdministrationController getEvents() Unauthorized");
		mvc.perform(get("/manage/rooms"))
			.andExpect(status().isForbidden());
	}

	@Test
	void testGetPageNotAuthorized() throws Exception {
		System.out.println("Testing EventAdministrationController getEvents() NotAuthorized");
		mvc.perform(get("/manage/rooms"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetEvents() throws Exception {
		System.out.println("Testing EventAdministrationController getEvents()");
		mvc.perform(get("/manage/rooms")
				.param("room", "3")
				.param("date", "2024-06-21"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Saal 2 am 21.06.2024")));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testAddEvent() throws Exception {
		System.out.println("Testing EventAdministrationController addEvents()");
		mvc.perform(post("/manage/rooms")
				.param("from", LocalDateTime.now().plusDays(14).toString())
				.param("to", LocalDateTime.now().plusDays(14).plusHours(2).toString())
				.param("room", "4")
				.param("eventname", "Test Event"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("successMessage", "Das neue Event wurde erfolgreich angelegt"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testAddEventToBeforeFrom() throws Exception {
		System.out.println("Testing EventAdministrationController addEvents() 'to' before 'from'");
		mvc.perform(post("/manage/rooms")
				.param("from", LocalDateTime.now().plusDays(14).toString())
				.param("to", LocalDateTime.now().plusDays(14).minusHours(2).toString())
				.param("room", "0")
				.param("eventname", "Test Event"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("errorMessage", "Der Startzeitpunkt muss vor dem Endzeitpunkt liegen."));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testAddEventInThePast() throws Exception {
		System.out.println("Testing EventAdministrationController addEvents() in the past");
		mvc.perform(post("/manage/rooms")
				.param("from", LocalDateTime.now().minusDays(14).toString())
				.param("to", LocalDateTime.now().minusDays(14).plusHours(2).toString())
				.param("room", "0")
				.param("eventname", "Test Event"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("errorMessage", "Das Event darf nicht in der Vergangenheit liegen."));
	}

}
