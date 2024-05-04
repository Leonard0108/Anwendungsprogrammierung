package kickstart.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "SEATS")
public class Seat implements Comparable<Seat>{
	private int raw;
	private int number;

	@Id
	private Long id;

	public enum SeatOccupancy {
		BOUGHT,
		RESERVED,
		FREE

	}

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
		return Objects.hash(getId(), getRaw(), getNumber());
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(!(object instanceof Seat seat))
			return false;

		return Objects.equals(getId(), seat.getId()) &&
			Objects.equals(getRaw(), seat.getRaw()) &&
			Objects.equals(getNumber(), seat.getNumber());
	}

	@Override
	public int compareTo(Seat seat) {
		int compareResult = Integer.compare(this.raw, seat.raw);
		if (compareResult == 0) {
			return Integer.compare(this.number, seat.number);
		}
		return compareResult;
	}
}
