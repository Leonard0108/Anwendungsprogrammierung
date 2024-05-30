package de.ufo.cinemasystem.additionalfiles;


import de.ufo.cinemasystem.models.UserEntry;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Streamable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.salespointframework.useraccount.UserAccountManagement;


import de.ufo.cinemasystem.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;



@Service
@Transactional
public class UserService {
	public static final Role                  USER_ROLE = Role.of("USER"); //Im Original Customer
	private final       UserRepository        userRepository;
	private final       UserAccountManagement userAccounts;
	private final       BCryptPasswordEncoder passwordEncoder;




	UserService(UserRepository userRepository, UserAccountManagement userAccounts, @Qualifier("passwordEncoder") BCryptPasswordEncoder passwordEncoder) {

		Assert.notNull(userRepository, "CustomerRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.userRepository = userRepository;
		this.userAccounts   = userAccounts;
		this.passwordEncoder = passwordEncoder;
	}



	/**
	 * Creates a new {@link UserEntry} using the information given in the {@link RegistrationForm}.
	 *
	 * @param form must not be {@literal null}.
	 */
	public void createUser(RegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		var password = Password.UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getName(), password, USER_ROLE);

		userRepository.save(new UserEntry(userAccount, form.getStreetName(), form.getStreetNumber(), form.getCity(), form.getPostalCode(), form.getState(), form.getCountry()));
	}




	public UserEntry createUser(UserAccount userAccount, String streetName, String streetNumber, String city, String postalCode, String state, String country) {
		return userRepository.save(new UserEntry(userAccount, streetName, streetNumber, city, postalCode, state, country));
	}


	/**
	 * Returns all {@link UserEntry}s currently available in the system.
	 *
	 * @return all {@link UserEntry} entities.
	 */
	public Streamable<UserEntry> findAll() {
		return userRepository.findAll();
	}


	public UserEntry loginBackground(LoginForm form)
	{
		UserEntry toCheckUserEntry;


		Assert.notNull(form, "Login form must not be null");
		toCheckUserEntry = userRepository.findByUserAccountUsername(LoginForm.getUserName());



		//Falls nicht funktionsfähig mal dehashen.
		if (toCheckUserEntry == null || passwordEncoder.matches(passwordEncoder.encode(LoginForm.getPassword()), String.valueOf(toCheckUserEntry.getUserAccount().getPassword()))) {
			return null;
		}



		return toCheckUserEntry;
	}



	public void deleteEmployee(UserEntry user) {
		UserAccount userAccount = user.getUserAccount();
		userRepository.delete(user);
		userAccounts.disable(userAccount.getId()); 		// before delete user account, disable it - if not springs crashes
		userAccounts.delete(userAccount);
	}




	public UserEntry getEmployeeById(Long id) {
		return userRepository.findById(id).orElse(null);
	}




	public void removeAllRoles(UserAccount userAccount) {
		for (Role role : userAccount.getRoles()) {
			userAccount.remove(role);
		}
		userAccounts.save(userAccount);
	}
}
