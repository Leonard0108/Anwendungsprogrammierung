package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;
import org.springframework.security.access.prepost.PreAuthorize;

public class EmployeeEntry extends UserEntry {
	private Money salary;
	private String jobMail;



	EmployeeEntry(UserEntry userEntry, Money salary, String jobMail)
	{
		super.setUserAccount(userEntry.getUserAccount());
		super.setStreetName(userEntry.getStreetName());
		super.setHouseNumber(userEntry.getHouseNumber());
		super.setPostalCode(userEntry.getPostalCode());
		super.setCity(userEntry.getCity());
		super.setState(userEntry.getState());
		super.setCountry(userEntry.getCountry());
		this.salary  = salary;
		this.jobMail = jobMail;
	}





	@PreAuthorize("BOSS")
	public void setSalary(Money salary) {
		this.salary = salary;
	}



	@PreAuthorize("BOSS")
	public Money getSalary() {
		return salary;
	}
}
