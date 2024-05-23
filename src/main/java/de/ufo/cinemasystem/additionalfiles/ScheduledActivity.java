package de.ufo.cinemasystem.additionalfiles;

import de.ufo.cinemasystem.models.CinemaHall;

import java.time.LocalDateTime;

public interface ScheduledActivity {
	long getId();
	LocalDateTime getStartDateTime();
	int getDuration();
	String getName();
	CinemaHall getCinemaHall();
	int compareTo(ScheduledActivity scheduledActivity);
}
