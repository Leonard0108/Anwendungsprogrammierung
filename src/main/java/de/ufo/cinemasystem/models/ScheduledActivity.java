package de.ufo.cinemasystem.models;

import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
