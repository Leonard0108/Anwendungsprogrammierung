package de.ufo.cinemasystem.additionalfiles;




import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;




@Getter
public class LoginForm {
	private static @NotEmpty String eMail, password;


	public LoginForm(String eMail, String password) {
		LoginForm.eMail    = eMail;
		LoginForm.password = password;
	}


	public static @NotEmpty String geteMail() {
		return eMail;
	}




	public static @NotEmpty String getPassword() {
		return password;
	}
}
