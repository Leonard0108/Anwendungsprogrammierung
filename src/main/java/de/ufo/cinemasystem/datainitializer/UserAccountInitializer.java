package de.ufo.cinemasystem.datainitializer;

import com.mysema.commons.lang.Assert;
import de.ufo.cinemasystem.additionalfiles.RegistrationForm;
import de.ufo.cinemasystem.additionalfiles.UserService;
import de.ufo.cinemasystem.repository.UserRepository;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.hibernate.internal.util.collections.ArrayHelper.forEach;


@Component
public class UserAccountInitializer implements DataInitializer {
	private static UserAccountManagement userAccountManagement;
	private static UserService userService;
	private static final Logger LOG = LoggerFactory.getLogger(UserAccountInitializer.class);
	private final UserRepository userRepository;


	public UserAccountInitializer(UserAccountManagement userAccountManagement, UserService userService, UserRepository userRepository) {
		Assert.notNull(userAccountManagement, "userAccountManagement must not be null");
		Assert.notNull(userService, "userService must not be null");


		UserAccountInitializer.userAccountManagement = userAccountManagement;
		UserAccountInitializer.userService = userService;
		this.userRepository = userRepository;
	}



	@Override
	public void initialize() {
		if (userAccountManagement.findByUsername("BOSS").isPresent()) {
			return;
		}



		LOG.info("Creating default users and customers.");


		UserAccount boss_account = userAccountManagement.create("boss", Password.UnencryptedPassword.of("123"), Role.of("BOSS"));
		UserAccount employee_account = userAccountManagement.create("em", Password.UnencryptedPassword.of("123"), Role.of("EMPLOYEE"));
		UserAccount authorized_employee = userAccountManagement.create("aem", Password.UnencryptedPassword.of("123"), Role.of("AUTHORIZED_EMPLOYEE"));


		var password = "123";

		//Personalkonten erstellen
		userService.createUser(boss_account,"Test", "Boss", "boss@ufo-cinema.de", "aslkdjflaksdf", "2", "adfladksf", "0000", "ajdfakfd", "Deutschland");
		userService.createUser(employee_account, "Test","Employee", "employee@ufo-cinema.de", "fdsffs","3","oedje", "11111", "dfsfffdf", "Deutschland");
		userService.createUser(authorized_employee, "Test Authorized", "Employee", "aemployee@ufo-cinema.de", "jhfdhjfdhj", "4", "jdjdj", "2222", "kjjkdfkje", "Deutschland");

		List.of(//
			new RegistrationForm("hans", "nicht", "Test", "lukasd2000@gmx.de", password, "Lange Str.", "9", "Gutenberg", "06193", "Sachsen-Anhalt", "Germany"),
			new RegistrationForm("Test", "User", "user", "user@email.com", password, "asdfasdf", "9", "asdfaf", "0000", "asdfasdf", "Deutschland")
			/*new RegistrationForm("mclovinfogell", "asdf@gmail.com", password, "Los Angeles", "asdfasdf", "asdfasdf", "sdfasdf", "sadfasdf", "asdfasdf")*/
		).forEach(userService::createUser);


		userRepository.findAll().forEach(tU -> {
			System.out.println(tU.toString());
			System.out.println("user name:" + tU.getUserAccount().getUsername());
			System.out.println("pw: " + tU.getUserAccount().getPassword());
			System.out.println("=========");
		});
	}
}
