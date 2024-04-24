package kickstart.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EVENTS")
public class Event {

	private @Id	@GeneratedValue long id;
	private String name;
	private String date;
	private String time;
	// Dauer in Minuten
	private int duration;

	public Event(String name, String date, String time, int duration) {
		this.name = name;
		this.date = date;
		this.time = time;
		this.duration = duration;
	}

	public Event() {}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

	public int getDuration() {
		return duration;
	}
}
