package de.ufo.cinemasystem.tests;


import de.ufo.cinemasystem.repository.EmployeeRepository;
import de.ufo.cinemasystem.repository.UserRepository;
import de.ufo.cinemasystem.services.EmployeeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GeneralTestEmployeeController {

	@Autowired
	MockMvc mvc;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmployeeService employeeService;

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testGetCreateEmployeePage() throws Exception {
		System.out.println("Testing EmployeeManagementController createEmployee");
		mvc.perform(get("/manage/createEmployee"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "aem", roles = "AUTHORIZED_EMPLOYEE")
	@Transactional
	void testGetCreateEmployeePageUnauthorized() throws Exception {
		System.out.println("Testing EmployeeManagementController createEmployee Unauthorized");
		mvc.perform(get("/manage/createEmployee"))
			.andExpect(status().isForbidden());
	}

	@Test
	void testGetCreateEmployeePageNotAuthorized() throws Exception {
		System.out.println("Testing EmployeeManagementController createEmployee NotAuthorized");
		mvc.perform(get("/manage/createEmployee"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testCreateEmployee() throws Exception {
		System.out.println("Testing EmployeeManagementController createEmployee");
		mvc.perform(post("/manage/createEmployee")
				.param("firstName", "John")
				.param("lastName", "Doe")
				.param("email", "john.doe@example.com")
				.param("job", "Manager")
				.param("salary", "50000")
				.param("hours", "40"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("createdUser", "Ein neuer Nutzer wurde erfolgreich angelegt"));
		// Add additional assertions to check if the employee was created in the repository if needed
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testCreateEmployeeWithErrors() throws Exception {
		System.out.println("Testing EmployeeManagementController createEmployee with errors");
		mvc.perform(post("/manage/createEmployee")
				.param("firstName", "John")
				.param("lastName", "")
				.param("email", "john.doe@example.com")
				.param("job", "Manager")
				.param("salary", "50000")
				.param("hours", "40"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("employeeRegistrationForm", "lastName"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testShowAllEmployees() throws Exception {
		System.out.println("Testing EmployeeManagementController showAllEmployees");
		mvc.perform(get("/manage/staff"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("employees")));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testEditUser() throws Exception {
		System.out.println("Testing EmployeeManagementController editUser");
		// Create a dummy employee and user to test editing
		// Add code here to create and save a dummy employee and user

		mvc.perform(get("/manage/editUser")
				.param("id", "dummy-uuid"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("employee", "user"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	@Transactional
	void testEditUserPost() throws Exception {
		System.out.println("Testing EmployeeManagementController editUser post");
		// Create a dummy employee and user to test editing
		// Add code here to create and save a dummy employee and user

		mvc.perform(post("/manage/editUser")
				.param("id", "dummy-uuid")
				.param("firstName", "John")
				.param("lastName", "Doe")
				.param("email", "john.doe@example.com")
				.param("job", "Manager")
				.param("salary", "60000")
				.param("hours", "35"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/manage/staff"));
	}
}
