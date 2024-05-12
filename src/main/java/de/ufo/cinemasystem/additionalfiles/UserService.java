package de.ufo.cinemasystem.additionalfiles;


import de.ufo.cinemasystem.models.UserEntry;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.salespointframework.useraccount.UserAccountManagement;


import de.ufo.cinemasystem.repository.UserRepository;


@Primary
@Service
public class UserService implements UserDetailsService {
	private final   String                USER_NOT_FOUND_MESSAGE = "User with e-mail %s not found.";
	private final   UserAccountManagement userAccountManagement;
	UserRepository                        userRepository;
	PasswordEncoder                       passwordEncoder;




	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserAccountManagement userAccountManagement) {
		this.userRepository        = userRepository;
		this.passwordEncoder       = passwordEncoder;
		this.userAccountManagement = userAccountManagement;
	}



	@Override
	public UserDetails loadUserByUsername(String eMail) throws UsernameNotFoundException {
		return userRepository.findByEmail(eMail).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, eMail)));
	}



	public boolean signUp(String eMail, String password, String forename, String name,
						  String streetAddress, Long houseNumber, String city, String state, String country, String phoneNumber)
	{
		if (userRepository.findByEmail(eMail).isEmpty())
		{
			UserEntry newUser = new UserEntry();
			newUser.setEmail(eMail);
			newUser.setPassword(passwordEncoder.encode(password));
			newUser.setName(forename);
			newUser.setLastName(name);
			newUser.setStreetAddress(streetAddress);
			newUser.setStreetNumber(houseNumber);
			newUser.setCity(city);
			newUser.setState(state);
			newUser.setCountry(country);
			newUser.setPhoneNumber(phoneNumber);

			userRepository.save(newUser);
			System.out.println("Hallo Welt.");

			return true;
		}
		else
		{
			return false;
		}
	}




	public boolean login(String eMail, String password) {
		String encodedPassword = passwordEncoder.encode(password);
		return userAccountManagement.findByUsername(eMail)
			.filter(account -> account.getPassword().equals(encodedPassword))
			.isPresent();
	}
}
