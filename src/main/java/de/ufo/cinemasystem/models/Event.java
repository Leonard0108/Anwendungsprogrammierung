package de.ufo.cinemasystem.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modellklasse für Besondere Events
 * @author Tobias Knoll
 */
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

        /**
         * Erstelle ein neues Event.
         * @param name Name
         * @param startDateTime Startzeitpunkt
         * @param duration Laufzeit
         */
	public Event(String name, LocalDateTime startDateTime, int duration) {
		this.name = name;
		this.startDateTime = startDateTime;
		this.duration = duration;
	}

        /**
         * Hibernate-Konstruktor. Bitte nicht benutzen, da die Instanzvariablen nicht gesetzt werden.
         */
	public Event() {}

        /**
         * Erhalte die Event-ID.
         * @return Event-ID
         */
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

        /**
         * Erhalte den Kinosaal
         * @return Kinosaal
         */
	@Override
	public CinemaHall getCinemaHall() {
		return cinemaHall;
	}

        /**
         * Kinosaal setzen.
         * @param cinemaHall neuer Kinosaal
         */
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
