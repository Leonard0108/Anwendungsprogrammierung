package de.ufo.cinemasystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Modellklasse für Sitze.
 * @author Yannick Harnisch
 */
@Entity
@Table(name = "SEATS")
public class Seat implements Comparable<Seat>{
	private int row;
	private int position;

	@Id
	private Long id;

        /**
         * Represents the occupation status of a seat. This information is only usefull in conjunction with a CinemaShow.
         */
	public enum SeatOccupancy {
            /**
             * the seat has been sold.
             */
		BOUGHT,
                /**
                 * someone reserved this seat.
                 */
		RESERVED,
                /**
                 * This seat is currently available.
                 */
		FREE
	}

        /**
         * Represents the place group of a seat. Place groups with lower numbers are more expensive.
         */
	public enum PlaceGroup {
            /**
             * The seat is in Place group one.
             */
		GROUP_1,
                /**
                 * The seat is in Place group two.
                 */
		GROUP_2,
                /**
                 * The seat is in Place group three.
                 */
		GROUP_3
	}

        /**
         * construct a new seat.
         * @param row the row of this seat (0-25)
         * @param position the position in the row (0-99)
         */
	public Seat(int row, int position) {
		this.row = row;
		this.position = position;
		this.id = 100L * row + position;
	}

        /**
         * Hibernate-only constructor. Do not use, you will break things.
         */
	public Seat() {}

        /**
         * Get the row.
         * @return the row
         */
	public int getRow() {
		return this.row;
	}

        /**
         * get the position
         * @return the position
         */
	public int getPosition() {
		return this.position;
	}

        /**
         * Get the id of this seat.
         * @return the id
         */
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
