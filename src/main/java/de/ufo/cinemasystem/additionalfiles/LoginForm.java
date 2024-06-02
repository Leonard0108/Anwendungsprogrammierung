package de.ufo.cinemasystem.additionalfiles;




import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;




@Getter
public class LoginForm {
	private static @NotEmpty String userName, password;


	public LoginForm(String userName, String password) {
		LoginForm.userName    = userName;
		LoginForm.password = password;
	}


	public static @NotEmpty String getUserName() {
		return userName;
	}




	public static @NotEmpty String getPassword() {
		return password;
	}
}
