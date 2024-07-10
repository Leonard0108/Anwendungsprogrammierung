package de.ufo.cinemasystem.services;

import de.ufo.cinemasystem.models.Event;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.ScheduledActivity;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Zusatzservice für alle ScheduledActivity's.
 * @author Tobias Knoll
 */
@Service
public class ScheduledActivityService {

	private EventRepository eventRepository;
	private CinemaShowRepository cinemaShowRepository;

        /**
         * Erstelle einen neuen Service, mit gegebenen Abhängigkeiten.
         * @param eventRepository Implementierung Event-Repository
         * @param cinemaShowRepository Implementierung CinemaShow-Repository
         */
	public ScheduledActivityService(EventRepository eventRepository, CinemaShowRepository cinemaShowRepository) {
		this.eventRepository = eventRepository;
		this.cinemaShowRepository = cinemaShowRepository;
	}

	/**
	 * Erfrage alle ScheduledActivity-Objekte.
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
	 *  Ruft alle CinemaShows und Events aus der Datenbank ab, die in einem bestimmten Zeitintervall und in einem bestimmten Kinosaal stattfinden,
	 *  und gibt sie als sortierte Liste von ScheduledActivity zurück.
	 *
	 * @param from der Beginn des Zeitintervalls
	 * @param to das Ende des Zeitintervalls
	 * @param roomId die ID des betrachteten Kinosaals
	 * @return eine sortierte Liste von ScheduledActivity-Objekten, die im angegebenen Zeitintervall stattfinden
	 */
	public List<ScheduledActivity> getActivitysInTimeInterval(LocalDateTime from, LocalDateTime to, long roomId) {

		List<ScheduledActivity> activitiesInInterval = getActivitysOnDay(from.toLocalDate());

		// entfernt alle Aktivitäten die nicht ins Intervall fallen
		activitiesInInterval.removeIf(activity -> activity.getCinemaHall().getId() != roomId || activity.getStartDateTime().plusMinutes(activity.getDuration()).isBefore(from) ||
			activity.getStartDateTime().isAfter(to));
		return activitiesInInterval;
	}


	/**
	 *
	 * Prüft, ob im angegebenen Zeitintervall und Saal keine Aktivitäten stattfinden.
	 *
	 * @param from der Beginn des Zeitintervalls
	 * @param to das Ende des Zeitintervalls
	 * @param roomId die ID des betrachteten Kinosaals
	 * @return true, wenn keine Aktivitäten im Zeitintervall stattfinden, andernfalls false
	 */
	public boolean isTimeSlotAvailable(LocalDateTime from, LocalDateTime to, long roomId){
		return getActivitysInTimeInterval(from, to, roomId).isEmpty();
	}

	/**
	 *
	 * Prüft, ob im angegebenen Zeitintervall und Saal keine Aktivitäten stattfinden.
	 *
	 * @param from der Beginn des Zeitintervalls
	 * @param to das Ende des Zeitintervalls
	 * @param roomId die ID des betrachteten Kinosaals
	 * @param withoutActivity zu ignorierende Activity (UseCase: Um die aktuelle Activity aus der Prüfung auszuschließen,
	 *                        z.B. bei Änderungen an einer bestehenden Activity)
	 * @return true, wenn keine Aktivitäten im Zeitintervall stattfinden, andernfalls false
	 */
	public boolean isTimeSlotAvailable(LocalDateTime from, LocalDateTime to, long roomId, ScheduledActivity withoutActivity){
		return getActivitysInTimeInterval(from, to, roomId).stream()
			.filter(a -> !a.equals(withoutActivity))
			.findAny().isEmpty();
	}

}
