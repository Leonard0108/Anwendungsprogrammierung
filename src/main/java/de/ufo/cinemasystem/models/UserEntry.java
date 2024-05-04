package de.ufo.cinemasystem.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.ufo.cinemasystem.additionalfiles.UserRole;

import java.util.Collection;
import java.util.Collections;



@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "user")
public class UserEntry implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long     id;
	Boolean  enabled;
	Boolean  locked;
	String   name;
	String   lastName;
	String   email;
	String   password;
	String   streetAddress;
	String   streetNumber;
	String   city;
	String   state;
	String   country;
	String   phoneNumber;
	@Enumerated(EnumType.STRING)
	UserRole userRole;





	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(userRole.name());
		return Collections.singletonList(simpleGrantedAuthority);
	}




	@Override
	public String getUsername() {
		return "";
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
