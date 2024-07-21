package de.ufo.cinemasystem.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BusinessDataDashboardControllerTest {

	@Autowired
	MockMvc mvc;


	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetPage() throws Exception {
		System.out.println("Testing BusinessDataDashboardController");
		mvc.perform(get("/statistics"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testGetPageUnauthorized() throws Exception {
		System.out.println("Testing BusinessDataDashboardController Unauthorized");
		mvc.perform(get("/statistics"))
			.andExpect(status().isForbidden());
	}

	@Test
	void testGetPageNotAuthorized() throws Exception {
		System.out.println("Testing BusinessDataDashboardController NotAuthorized");
		mvc.perform(get("/statistics"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetDataForStatistics() throws Exception {
		System.out.println("Testing BusinessDataDashboardController StatisticData");
		mvc.perform(get("/statistics"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("revenueData", not(empty())))
			.andExpect(model().attribute("dailyIncomeData", not(empty())));
	}





}
