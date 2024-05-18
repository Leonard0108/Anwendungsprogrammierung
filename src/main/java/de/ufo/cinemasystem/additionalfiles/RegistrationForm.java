package de.ufo.cinemasystem.additionalfiles;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class RegistrationForm {
	private final @NotEmpty String name, eMail, password, address;

	public RegistrationForm(String name, String eMail, String password, String address) {
		this.name = name;
		this.eMail = eMail;
		this.password = password;
		this.address = address;
	}
}
