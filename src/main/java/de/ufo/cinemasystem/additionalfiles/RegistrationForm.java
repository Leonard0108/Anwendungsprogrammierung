package de.ufo.cinemasystem.additionalfiles;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;




@Getter
public class RegistrationForm {
	private final @NotEmpty String name, username, eMail, password, streetName, streetNumber, city, postalCode, state, country;

	public RegistrationForm(String name, String username, String eMail, String password, String streetName, String streetNumber, String city, String postalCode, String state, String country ) {
		this.name = name;
		this.username = username;
		this.eMail = eMail;
		this.password = password;
		this.streetName = streetName;
		this.streetNumber = streetNumber;
		this.city = city;
		this.postalCode = postalCode;
		this.state = state;
		this.country = country;
	}
}
