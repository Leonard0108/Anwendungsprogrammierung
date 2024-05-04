package kickstart.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "EVENTS")
public class Event implements Comparable<Event>{

	private @Id	@GeneratedValue long id;
	private @NotEmpty String name;
	private LocalDateTime startDateTime;
	// Dauer in Minuten
	private int duration;
	@ManyToOne
	@JoinColumn(name = "cinema_hall_id")
	private CinemaHall cinemaHall;

	public Event(String name, LocalDateTime startDateTime, int duration, CinemaHall cinemaHall) {
		this.name = name;
		this.startDateTime = startDateTime;
		this.duration = duration;
		this.cinemaHall = cinemaHall;
	}

	public Event() {}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public int getDuration() {
		return duration;
	}

	public CinemaHall getCinemaHall() {
		return cinemaHall;
	}

	@Override
	public int hashCode() {
		return Math.toIntExact(this.id);
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(object instanceof Event event) {
			return Objects.equals(event.getId(), this.getId());
		}
		return false;
	}
	@Override
	public int compareTo(Event event) {
		return this.getStartDateTime().compareTo(event.getStartDateTime());
	}
}
