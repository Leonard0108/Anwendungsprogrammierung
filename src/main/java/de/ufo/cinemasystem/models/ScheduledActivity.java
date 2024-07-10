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
 * Interface fasst CinemaShows und Events zusammen
 */
public interface ScheduledActivity {
    
        /**
         * ID dieser ScheduledActivity
         * @return ID
         */
	long getId();
        /**
         * Erhalte den Startzeitpunkt dieser ScheduledActivity.
         * @return Startzeitpunkt
         */
	LocalDateTime getStartDateTime();
        /**
         * Erhalte die Länge dieser ScheduledActivity, in Minuten.
         * @return Länge
         */
	int getDuration();
        /**
         * Name dieser ScheduledActivity
         * @return Name
         */
	String getName();
        
        /**
         * Kinosaal, in der diese ScheduledActivity Stattfindet
         * @return Kinosaal
         */
	CinemaHall getCinemaHall();
        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object) 
         * 
         * @param scheduledActivity Zu vergleichende ScheduledActivity
         * @return Ganzzahl
         */
	int compareTo(ScheduledActivity scheduledActivity);
}
