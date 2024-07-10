package de.ufo.cinemasystem.models;




import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.util.UUID;

import org.jmolecules.ddd.types.Identifier;
import org.salespointframework.core.AbstractAggregateRoot;
import org.salespointframework.useraccount.UserAccount;


/**
 * Entry, der die Daten des Nutzers speichert. Name und E-Mail könnte noch in UserAccount gepackt werden. Variablen sind dort
 * bereits vorhanden.
 * Weitere enthaltene Variablen:
 * streetName, houseNumber, city, state, postalCode und country, alles Strings, da so auch die Korrektheit besser überprüft werden
 * kann.
 * Des Weiteren ist eine eingebettete Klasse vorhanden, welche eine Nutzer-ID generiert, welche gleichzeitig der Primär-
 * schlüssel ist.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user")
public class UserEntry extends AbstractAggregateRoot<UserEntry.UserIdentifier>   {

	private @EmbeddedId UserIdentifier id = new UserIdentifier();

	private String firstName, lastName;
	private String eMail, streetName, houseNumber, city, state, postalCode, country;

	// (｡◕‿◕｡)
	// Jedem Customer ist genau ein UserAccount zugeordnet, um später über den UserAccount an den
	// Customer zu kommen, speichern wir den hier
	@OneToOne //
	private UserAccount userAccount;

        /**
         * Erstelle einen neuen Nutzer.
         * @param userAccount Salespoint-Konto
         * @param firstName Vorname
         * @param lastName Nachname
         * @param eMail E-Mail
         * @param streetName Straße
         * @param houseNumber Hausnummer
         * @param city Stadt
         * @param postalCode PLZ
         * @param state Bundesland (oder vergleichbar)
         * @param country Staat
         */
	public UserEntry(UserAccount userAccount, String firstName, String lastName, String eMail, String streetName, String houseNumber, String city, String postalCode, String state, String country) {

		this.userAccount = userAccount;
		this.firstName   = firstName;
		this.lastName    = lastName;
		this.eMail       = eMail;
		this.streetName  = streetName;
		this.houseNumber = houseNumber;
		this.city        = city;
		this.postalCode  = postalCode;
		this.state       = state;
		this.country     = country;
	}


        /**
         * Identifiziert einen Nutzer.
         */
	@Embeddable
	public static final class UserIdentifier implements Identifier, Serializable {

		private static final long serialVersionUID = 7740660930809051850L;

                /**
                 * internal id
                 */
		private final UUID identifier;

		/**
		 * Creates a new unique identifier for {@link UserEntry}s.
		 */
		UserIdentifier() {
			this(UUID.randomUUID());
		}

		/**
		 * Only needed for property editor, shouldn't be used otherwise.
		 *
		 * @param identifier The string representation of the identifier.
		 */
		UserIdentifier(UUID identifier) {
			this.identifier = identifier;
		}

                /**
                 * Get the UUID.
                 * @return the UUId.
                 */
		public UUID getId() {
			return this.identifier;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;

			result = prime * result + (identifier == null ? 0 : identifier.hashCode());

			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {

			if (obj == this) {
				return true;
			}

			if (!(obj instanceof UserIdentifier that)) {
				return false;
			}

			return this.identifier.equals(that.identifier);
		}
	}
}
