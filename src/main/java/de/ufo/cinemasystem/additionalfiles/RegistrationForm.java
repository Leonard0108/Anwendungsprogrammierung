package de.ufo.cinemasystem.additionalfiles;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class RegistrationForm {
	private final @NotEmpty String name, eMail, password, address;

}
