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


	@Embeddable
	public static final class UserIdentifier implements Identifier, Serializable {

		private static final long serialVersionUID = 7740660930809051850L;
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
