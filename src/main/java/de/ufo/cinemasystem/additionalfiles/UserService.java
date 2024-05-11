package de.ufo.cinemasystem.additionalfiles;


import de.ufo.cinemasystem.models.UserEntry;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import de.ufo.cinemasystem.repository.UserRepository;

import java.util.Optional;


@Primary
@Service
public class UserService implements UserDetailsService {
	private final String USER_NOT_FOUND_MESSAGE = "User with e-mail %s not found.";
	UserRepository userRepository;
	PasswordEncoder passwordEncoder;
	private final UserAccountManager userAccountManager;




	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}



	@Override
	public UserDetails loadUserByUsername(String eMail) throws UsernameNotFoundException {
		return userRepository.findByEmail(eMail).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, eMail)));
	}



	public boolean signUp(String eMail, String password, String forename, String name,
						  String streetAddress, Long houseNumber, String city, String state, String country, String phoneNumber)
	{
		if (!userRepository.findByEmail(eMail).isPresent())
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
}
