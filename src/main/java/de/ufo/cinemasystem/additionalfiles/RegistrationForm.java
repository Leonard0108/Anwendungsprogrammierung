package de.ufo.cinemasystem.additionalfiles;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;



/**
 * Modell-Repräsentation des Nutzer-Registrierungsformulars
 * @author Lukas Dietrich
 */
@Getter
public class RegistrationForm {
	private final @NotEmpty String firstName, lastName, username, eMail, password, streetName, streetNumber, city, postalCode, state, country;

        /**
         * Lege ein neues Formular mit folgenden Parametern an:
         * @param firstName Vorname
         * @param lastName Nachname
         * @param username Nutzername
         * @param eMail E-Mail
         * @param password Passwort
         * @param streetName Straße
         * @param streetNumber Hausnummer
         * @param city Stadt
         * @param postalCode PLZ
         * @param state Bundesland (oder vergleichbar)
         * @param country Staat
         */
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
