package de.ufo.cinemasystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "SEATS")
public class Seat implements Comparable<Seat>{
	private int row;
	private int position;

	@Id
	private Long id;

	public enum SeatOccupancy {
		BOUGHT,
		RESERVED,
		FREE
	}

	public enum PlaceGroup {
		GROUP_1,
		GROUP_2,
		GROUP_3
	}

	public Seat(int row, int position) {
		this.row = row;
		this.position = position;
		this.id = 100L * row + position;
	}

	public Seat() {}

	public int getRow() {
		return this.row;
	}

	public int getPosition() {
		return this.position;
	}

	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getRow(), getPosition());
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(!(object instanceof Seat seat))
			return false;

		return Objects.equals(getId(), seat.getId()) &&
			Objects.equals(getRow(), seat.getRow()) &&
			Objects.equals(getPosition(), seat.getPosition());
	}

	@Override
	public int compareTo(Seat seat) {
		int compareResult = Integer.compare(this.row, seat.row);
		if (compareResult == 0) {
			return Integer.compare(this.position, seat.position);
		}
		return compareResult;
	}
}
