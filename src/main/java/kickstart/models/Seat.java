package kickstart.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "SEATS")
public class Seat {
	private int raw;
	private int number;

	@Id
	private Long id;

	public Seat(int raw, int number) {
		this.raw = raw;
		this.number = number;
		this.id = 100L * raw + number;
	}

	public Seat() {}

	public int getRaw() {
		return this.raw;
	}

	public int getNumber() {
		return this.number;
	}

	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Math.toIntExact(this.id);
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(object instanceof Seat place) {
			return Objects.equals(place.getId(), this.getId());
		}
		return false;
	}
}
