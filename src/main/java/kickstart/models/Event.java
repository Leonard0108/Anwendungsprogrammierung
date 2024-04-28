package kickstart.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "EVENTS")
public class Event {

	private @Id	@GeneratedValue long id;
	private String name;
	private LocalDateTime startDateTime;
	// Dauer in Minuten
	private int duration;

	public Event(String name, LocalDateTime startDateTime, int duration) {
		this.name = name;
		this.startDateTime = startDateTime;
		this.duration = duration;
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

	public int compareTo(Event event) {
		return this.getStartDateTime().compareTo(event.getStartDateTime());
	}
}
