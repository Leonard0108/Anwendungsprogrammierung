package de.ufo.cinemasystem.additionalfiles;




import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;



/**
 * Modell-Repräsentation des Anmeldeformulars
 * @author Lukas Dietrich
 */
@Getter
public class LoginForm {
	private static @NotEmpty String userName, password;


        /**
         * Erstelle ein neues Formular.
         * @param userName Nutzername
         * @param password Passwort
         */
	public LoginForm(String userName, String password) {
		LoginForm.userName    = userName;
		LoginForm.password = password;
	}


        /**
         * Erhalte den Nutzernamen.
         * @return Nutzername
         */
	public static @NotEmpty String getUserName() {
		return userName;
	}




        /**
         * Erhalte das Passwort
         * @return Passwort
         */
	public static @NotEmpty String getPassword() {
		return password;
	}
}
