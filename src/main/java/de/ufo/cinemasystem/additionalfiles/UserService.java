package de.ufo.cinemasystem.additionalfiles;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.ufo.cinemasystem.repository.UserRepository;


//@Service
public class UserService implements UserDetailsService {
	UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String eMail) throws UsernameNotFoundException {
		return userRepository.findByEmail(eMail).orElseThrow(() -> new UsernameNotFoundException(eMail));
	}
}
