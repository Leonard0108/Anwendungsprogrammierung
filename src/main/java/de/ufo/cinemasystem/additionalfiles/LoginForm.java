package de.ufo.cinemasystem.additionalfiles;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;


@Getter
public class LoginForm {
	private final @NotEmpty String eMail, password;




	LoginForm(String eMail, String password) {
		this.eMail = eMail;
		this.password = password;
	}
}
