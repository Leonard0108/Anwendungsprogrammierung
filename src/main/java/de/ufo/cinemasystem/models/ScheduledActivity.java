package de.ufo.cinemasystem.models;

import java.time.LocalDateTime;

/**
 * Interface fasst CineaShows und Events zusammen
 */
public interface ScheduledActivity {
	long getId();
	LocalDateTime getStartDateTime();
	int getDuration();
	String getName();
	CinemaHall getCinemaHall();
	int compareTo(ScheduledActivity scheduledActivity);
}
