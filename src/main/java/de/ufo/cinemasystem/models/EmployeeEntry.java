package de.ufo.cinemasystem.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.Money;
import org.springframework.data.annotation.Id;
import org.springframework.security.access.prepost.PreAuthorize;


/**
 * Entity, welche als Erweiterung zum User-Entry dient und noch diverse Daten für die Mitarbeiter bereit hält.
 * Hierzu gehören das Gehalt, eine Firmen-E-Mail, Wochenstunden, ob der Mitarbeiter noch eingestellt ist oder nicht u.e.m..
 */
@Getter
@Setter
@Entity
@Table(name = "employees")
public class EmployeeEntry {
	private             Money                    salary;
	private             String                   jobMail;
	private             short                    hoursPerWeek;
	private             boolean                  stillAdjusted;
	private             String                   shift;
	private @EmbeddedId UserEntry.UserIdentifier id;



        /**
         * Erstellt einen neuen Mitarbeiter mit den angegebenen Parametern.
         * @param userEntry Nutzerkonto
         * @param salary Monatsgehalt
         * @param jobMail Job-Email
         * @param hoursPerWeek Wochenstunden
         */
	public EmployeeEntry(UserEntry userEntry, Money salary, String jobMail, short hoursPerWeek)
	{
		this.id            = userEntry.getId();
		this.salary        = salary;
		this.jobMail       = jobMail;
		this.hoursPerWeek  = hoursPerWeek;
		this.stillAdjusted = true;
		this.shift         = "0800 - 1700";
	}

        /**
         * Hibernate-Konstruktor. Bitte nicht benutzen, da die Instanzvariablen nicht gesetzt werden.
         */
	public EmployeeEntry() {

	}


        /**
         * Monatzgehalt setzen.
         * @param salary Monatsgehalt
         */
	@PreAuthorize("BOSS")
	public void setSalary(Money salary) {
		this.salary = salary;
	}



        /**
         * Erhalte das Monatsgehalt.
         * @return Monatsgehalt
         */
	@PreAuthorize("BOSS")
	public Money getSalary() {
		return salary;
	}
}
