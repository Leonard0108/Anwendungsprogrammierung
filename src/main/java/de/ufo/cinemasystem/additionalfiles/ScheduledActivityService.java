package de.ufo.cinemasystem.additionalfiles;

import de.ufo.cinemasystem.models.Event;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.EventRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ScheduledActivityService {

	private EventRepository eventRepository;
	private CinemaShowRepository cinemaShowRepository;

	public ScheduledActivityService(EventRepository eventRepository, CinemaShowRepository cinemaShowRepository) {
		this.eventRepository = eventRepository;
		this.cinemaShowRepository = cinemaShowRepository;
	}

	public List<ScheduledActivity> getAllActivitysSorted() {

		List<Event> events = eventRepository.findAll().toList();
		List<CinemaShow> cinemaShows = cinemaShowRepository.findAll().toList();

		List<ScheduledActivity> allActivities = new ArrayList<>();
		allActivities.addAll(events);
		allActivities.addAll(cinemaShows);

		// Sortiert die Liste nach StartDateTime
		allActivities.sort(Comparator.comparing(ScheduledActivity::getStartDateTime));

		return allActivities;
	}

	public List<ScheduledActivity> getOnDayActivitysSorted(LocalDate date) {

		List<Event> events = eventRepository.findEventShowsOnDay(date).toList();
		List<CinemaShow> cinemaShows = cinemaShowRepository.findCinemaShowsOnDay(date).toList();

		List<ScheduledActivity> allActivities = new ArrayList<>();
		allActivities.addAll(events);
		allActivities.addAll(cinemaShows);

		// Sortiert die Liste nach StartDateTime
		allActivities.sort(Comparator.comparing(ScheduledActivity::getStartDateTime));

		return allActivities;
	}
}
