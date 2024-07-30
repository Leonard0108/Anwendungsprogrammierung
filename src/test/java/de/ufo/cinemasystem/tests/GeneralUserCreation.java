package de.ufo.cinemasystem.tests;



import de.ufo.cinemasystem.additionalfiles.RegistrationForm;
import de.ufo.cinemasystem.models.UserEntry;
import de.ufo.cinemasystem.repository.UserRepository;
import de.ufo.cinemasystem.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class GeneralUserCreation
{
	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UserService userService;




	@BeforeEach
	void setup() {
		// Mocking findAll to return an empty stream
		when(userRepository.findAll()).thenReturn((Streamable<UserEntry>) Stream.empty());
	}
	@Test
	void testRegisterGet() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/register"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/registration"));
	}

	@Test
	void testRegistrationGet() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/registration"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("title"))
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegistrationPostSuccess() throws Exception {
		RegistrationForm form = new RegistrationForm("Hans", "Nicht", "Test", "hans@domain.com", "password", "Street", "9", "City", "12345", "State", "Germany");
		when(userService.createUser(Mockito.any())).thenReturn((short) 0);

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("createdUser", "Ein neuer Nutzer wurde erfolgreich angelegt"))
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	void testRegistrationPostSuccess2() throws Exception {
		RegistrationForm form = new RegistrationForm("Peter", "Parer", "Test2", "hans@@ufo-kino.de", "password", "Street", "9", "City", "12345", "State", "Germany");
		when(userService.createUser(Mockito.any())).thenReturn((short) 0);

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("createdUser", "Ein neuer Nutzer wurde erfolgreich angelegt"))
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	void testRegistrationPostEmailExists() throws Exception {
		RegistrationForm form = new RegistrationForm("Hans", "Nicht", "Test", "hans@domain.com", "password", "Street", "9", "City", "12345", "State", "Germany");
		when(userService.createUser(Mockito.any())).thenReturn((short) 1);

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().attribute("error", "Diese E-Mail-Adresse wird bereits verwendet!."))
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegistrationPostUsernameExists() throws Exception {
		RegistrationForm form = new RegistrationForm("Hans", "Nicht", "Test", "hans@domain.com", "password", "Street", "9", "City", "12345", "State", "Germany");
		when(userService.createUser(Mockito.any())).thenReturn((short) 2);

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().attribute("error", "Dieser Benutzername ist bereits vergeben."))
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegistrationPostUnknownEmailProvider() throws Exception {
		RegistrationForm form = new RegistrationForm("Hans", "Nicht", "Test", "hans@unknown.com", "password", "Street", "9", "City", "12345", "State", "Germany");
		when(userService.createUser(Mockito.any())).thenReturn((short) 3);

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().attribute("error", "Unbekannter E-Mail-Provider. Bitte Schreibweise prüfen."))
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegistrationPostInvalidPostalCode() throws Exception {
		RegistrationForm form = new RegistrationForm("Hans", "Nicht", "Test", "hans@domain.com", "password", "Street", "9", "City", "ABCDE", "State", "Germany");
		when(userService.createUser(Mockito.any())).thenReturn((short) 4);

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().attribute("error", "Ungültige Postleitzahl!"))
			.andExpect(view().name("registration"));
	}

	@Test
	void testGetRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/role"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("title"))
			.andExpect(view().name("roletest"));
	}

	@Test
	void testLogout() throws Exception {
		mvc.perform(post("/logout"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void testCustomerListWithRoleUSER() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/customers"))
			.andExpect(status().isForbidden());
	}

	@Test
	void testInvalidUserRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/role"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", "Rollencheck"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testRegisterWithBossRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/register"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/registration"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testRegistrationWithErrors() throws Exception {
		RegistrationForm form = new RegistrationForm("", "Nicht", "Test", "hans@domain.com", "password", "Street", "9", "City", "ABCDE", "State", "Germany");

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegisterViewAttributes() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/registration"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("title"))
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegistrationModelAttributes() throws Exception {
		mvc.perform(post("/registration")
				.param("firstName", "Hans")
				.param("lastName", "Nicht")
				.param("username", "Test")
				.param("email", "hans@domain.com")
				.param("password", "password")
				.param("street", "Street")
				.param("houseNumber", "9")
				.param("city", "City")
				.param("postalCode", "12345")
				.param("state", "State")
				.param("country", "Germany"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"))
			.andExpect(flash().attributeExists("createdUser"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testRegisterRedirect() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/register"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/registration"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testCustomerListRedirection() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/customers"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("customerList"))
			.andExpect(view().name("welcome"));
	}

	@Test
	void testLogoutRedirection() throws Exception {
		mvc.perform(post("/logout"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void testCustomersWithUserRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/customers"))
			.andExpect(status().isForbidden());
	}

	@Test
	void testRegisterRedirectWithoutAuth() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/register"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/registration"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testRegistrationWithInvalidEmail() throws Exception {
		RegistrationForm form = new RegistrationForm("Hans", "Nicht", "Test", "hans@invalid.com", "password", "Street", "9", "City", "12345", "State", "Germany");
		when(userService.createUser(Mockito.any())).thenReturn((short) 3);

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().attribute("error", "Unbekannter E-Mail-Provider. Bitte Schreibweise prüfen."))
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegistrationFormBindingErrors() throws Exception {
		RegistrationForm form = new RegistrationForm("", "", "Test", "hans@domain.com", "password", "Street", "9", "City", "ABCDE", "State", "Germany");

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegisterPageTitle() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/registration"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("title"))
			.andExpect(view().name("registration"));
	}

	@Test
	void testLogoutInvalidatesSession() throws Exception {
		mvc.perform(post("/logout"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testRoleCheckWithBossRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/role"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("title"))
			.andExpect(view().name("roletest"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "USER")
	void testRoleCheckWithUserRole() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/role"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("title"))
			.andExpect(view().name("roletest"));
	}

	@Test
	void testCustomerListWithoutAuth() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/customers"))
			.andExpect(status().isForbidden());
	}
	@Test
	void testLoginPageLoadsSuccessfully() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("login"));
	}

	@Test
	void testLoginPostSuccess() throws Exception {
		mvc.perform(post("/login")
				.param("username", "Test")
				.param("password", "123"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testAccessCustomerListAsBoss() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/customers"))
			.andExpect(status().isOk())
			.andExpect(view().name("welcome"));
	}

	@Test
	@WithMockUser(username = "employee", roles = "EMPLOYEE")
	void testAccessCustomerListAsEmployee() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/customers"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testRegistrationWithBossRoleHasError() throws Exception {
		RegistrationForm form = new RegistrationForm("", "", "Test", "hans@domain.com", "password", "Street", "9", "City", "12345", "State", "Germany");

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("registration"));
	}

	@Test
	void testAccessCustomersWithoutAuth() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/customers"))
			.andExpect(status().isForbidden());
	}

	@Test
	void testAccessRegisterWithoutAuth() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/register"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/registration"));
	}

	@Test
	void testAccessRoleWithoutAuth() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/role"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void testAccessRoleAsUser() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/role"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", "Rollencheck"))
			.andExpect(view().name("roletest"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void testAccessRoleAsBoss() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/role"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("title", "Rollencheck"))
			.andExpect(view().name("roletest"));
	}

	@Test
	void testAccessRegistrationPage() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/registration"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("title"))
			.andExpect(view().name("registration"));
	}

	@Test
	void testRegistrationWithBindingErrors() throws Exception {
		RegistrationForm form = new RegistrationForm("", "", "Test", "hans@domain.com", "password", "Street", "9", "City", "ABCDE", "State", "Germany");

		mvc.perform(post("/registration")
				.flashAttr("registrationForm", form))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("registration"));
	}
}
