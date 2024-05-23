package de.ufo.cinemasystem.models;


import de.ufo.cinemasystem.additionalfiles.ScheduledActivity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "EVENTS")
public class Event implements Comparable<Event>, ScheduledActivity {

	private @Id	@GeneratedValue long id;
	private @NotEmpty String name;
	private LocalDateTime startDateTime;
	// Dauer in Minuten
	private int duration;
	@ManyToOne
	@JoinColumn(name = "cinema_hall_id")
	private CinemaHall cinemaHall;

	public Event(String name, LocalDateTime startDateTime, int duration) {
		this.name = name;
		this.startDateTime = startDateTime;
		this.duration = duration;
	}

	public Event() {}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	@Override
	public int getDuration() {
		return duration;
	}

	@Override
	public CinemaHall getCinemaHall() {
		return cinemaHall;
	}

	public void setCinemaHall(CinemaHall cinemaHall) {
		this.cinemaHall = cinemaHall;
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

	@Override
	public int compareTo(ScheduledActivity scheduledActivity) {
		return this.getStartDateTime().compareTo(scheduledActivity.getStartDateTime());
	}
}
