package de.ufo.cinemasystem.additionalfiles;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class EmployeeRegistrationForm {
	private final @NotNull String firstName, lastName, username, eMail, password, streetName, houseNumber, city, postalCode, state, country, jobMail, salary, hoursPerWeek;

	public EmployeeRegistrationForm(String firstName, String lastName, String username, String eMail, String password, String streetName, String houseNumber, String city, String postalCode, String state, String country, String salary,
									String jobMail, String hoursPerWeek) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.eMail = eMail;
		this.password = password;
		this.streetName = streetName;
		this.houseNumber = houseNumber;
		this.city = city;
		this.postalCode = postalCode;
		this.state = state;
		this.country = country;
		this.salary = salary;
		this.jobMail = jobMail;
		this.hoursPerWeek = hoursPerWeek;
	}
}
