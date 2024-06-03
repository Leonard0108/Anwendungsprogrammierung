package de.ufo.cinemasystem.additionalfiles;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;




@Getter
public class RegistrationForm {
	private final @NotEmpty String firstName, lastName, username, eMail, password, streetName, streetNumber, city, postalCode, state, country;

	public RegistrationForm(String firstName, String lastName, String username, String eMail, String password, String streetName, String streetNumber, String city, String postalCode, String state, String country ) {
		this.firstName = firstName;
		this.lastName = lastName;
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
