package de.ufo.cinemasystem.datainitializer;

import com.mysema.commons.lang.Assert;
import de.ufo.cinemasystem.additionalfiles.RegistrationForm;
import de.ufo.cinemasystem.additionalfiles.UserService;
import de.ufo.cinemasystem.models.UserEntry;
import de.ufo.cinemasystem.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;


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


		userAccountManagement.create("boss", Password.UnencryptedPassword.of("123"), Role.of("BOSS"));
		userAccountManagement.create("user", Password.UnencryptedPassword.of("123"), Role.of("USER"));
		userAccountManagement.create("em", Password.UnencryptedPassword.of("123"), Role.of("EMPLOYEE"));
		userAccountManagement.create("aem", Password.UnencryptedPassword.of("123"), Role.of("AUTHORIZED_EMPLOYEE"));


		var password = "123";

		List.of(//
			new RegistrationForm("hans", "lukasd2000@gmx.de", password, "Lange Str.", "9", "Gutenberg", "06193", "Sachsen-Anhalt", "Germany"),
			new RegistrationForm("dextermorgan","adf@gmail.de", password, "Miami-Dade County", "asdfasdf", "asdfasfd", "asdfaf", "asdfasfd", "asdfasdf"),
			new RegistrationForm("Anne Panzer", "annep2003@gmx.de", password, "Camden County - Motel", "aslkdjflaksdf", "ajskldfaklsf", "adfladksf", "kljsdfal", "ajdfakfd"),
			new RegistrationForm("mclovinfogell", "asdf@gmail.com", password, "Los Angeles", "asdfasdf", "asdfasdf", "sdfasdf", "sadfasdf", "asdfasdf")//
		).forEach(userService::createUser);


		userRepository.findAll().forEach(tU -> {
			System.out.println(tU.toString());
			System.out.println("user name:" + tU.getUserAccount().getUsername());
			System.out.println("pw: " + tU.getUserAccount().getPassword());
			System.out.println("=========");
		});
	}
}
