package kickstart.UserAuthentication;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String name;
	String  lastName;
	String email;
	String password;
	String streetAddress;
	String streetNumber;
	String city;
	String state;
	String country;

	UserEntry(Long id)
	{
		this.id = id;
	}

	public UserEntry(Long id, String name, String  lastName,
	String email, String password, String streetAddress, String streetNumber,
	String city, String state, String country) {
		this.id = id;
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.streetAddress = streetAddress;
		this.streetNumber = streetNumber;
		this.city = city;
		this.state = state;
		this.country = country;
	}

	public UserEntry() {

	}
}
