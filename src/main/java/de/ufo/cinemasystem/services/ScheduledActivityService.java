package de.ufo.cinemasystem.services;

import de.ufo.cinemasystem.models.Event;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.ScheduledActivity;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.EventRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// @Service
public class ScheduledActivityService {

	private EventRepository eventRepository;
	private CinemaShowRepository cinemaShowRepository;

	public ScheduledActivityService(EventRepository eventRepository, CinemaShowRepository cinemaShowRepository) {
		this.eventRepository = eventRepository;
		this.cinemaShowRepository = cinemaShowRepository;
	}

	/**
	 *
	 * @return Alle ScheduledActivity Objekte aus der Datenbank, in einer geordneten List
	 */
	public List<ScheduledActivity> getAllActivitys() {

		List<Event> events = eventRepository.findAll().toList();
		List<CinemaShow> cinemaShows = cinemaShowRepository.findAll().toList();

		List<ScheduledActivity> allActivities = new ArrayList<>();
		allActivities.addAll(events);
		allActivities.addAll(cinemaShows);

		// Sortiert die Liste nach StartDateTime
		allActivities.sort(Comparator.comparing(ScheduledActivity::getStartDateTime));

		return allActivities;
	}

	/**
	 *
	 * Ruft alle CinemaShows und Events aus der Datenbank für ein gegebenes Datum ab
	 * und gibt sie als sortierte Liste von ScheduledActivity zurück.
	 *
	 * @param date das Datum, für das die Aktivitäten abgerufen werden sollen
	 * @return eine sortierte Liste von ScheduledActivity-Objekten für das angegebene Datum
	 */
	public List<ScheduledActivity> getActivitysOnDay(LocalDate date) {

		List<Event> events = eventRepository.findEventShowsOnDay(date).toList();
		List<CinemaShow> cinemaShows = cinemaShowRepository.findCinemaShowsOnDay(date).toList();

		List<ScheduledActivity> activitysOnDay = new ArrayList<>();
		activitysOnDay.addAll(events);
		activitysOnDay.addAll(cinemaShows);

		// Sortiert die Liste nach StartDateTime
		activitysOnDay.sort(Comparator.comparing(ScheduledActivity::getStartDateTime));

		return activitysOnDay;
	}

	/**
	 *
	 *  Ruft alle CinemaShows und Events aus der Datenbank ab, die in einem bestimmten Zeitintervall stattfinden,
	 *  und gibt sie als sortierte Liste von ScheduledActivity zurück.
	 *
	 * @param from der Beginn des Zeitintervalls
	 * @param to das Ende des Zeitintervalls
	 * @return eine sortierte Liste von ScheduledActivity-Objekten, die im angegebenen Zeitintervall stattfinden
	 */
	public List<ScheduledActivity> getActivitysInTimeInterval(LocalDateTime from, LocalDateTime to) {

		List<ScheduledActivity> activitiesInInterval = getActivitysOnDay(from.toLocalDate());

		// entfernt alle Aktivitäten die nicht ins Intervall fallen
		activitiesInInterval.removeIf(activity -> activity.getStartDateTime().plusMinutes(activity.getDuration()).isBefore(from) ||
			activity.getStartDateTime().isAfter(to));
		return activitiesInInterval;
	}

	/**
	 *
	 * Prüft, ob im angegebenen Zeitintervall keine Aktivitäten stattfinden.
	 *
	 * @param from der Beginn des Zeitintervalls
	 * @param to das Ende des Zeitintervalls
	 * @return true, wenn keine Aktivitäten im Zeitintervall stattfinden, andernfalls false
	 */
	public boolean isTimeSlotAvailable(LocalDateTime from, LocalDateTime to){
		return getActivitysInTimeInterval(from, to).isEmpty();
	}

}
