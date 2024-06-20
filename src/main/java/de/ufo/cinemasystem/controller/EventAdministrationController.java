package de.ufo.cinemasystem.controller;


import de.ufo.cinemasystem.models.ScheduledActivity;
import de.ufo.cinemasystem.services.ScheduledActivityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.Event;
import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.EventRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

@Controller
public class EventAdministrationController {

	private EventRepository eventRepository;
	private CinemaHallRepository cinemaHallRepository;
	private ScheduledActivityService scheduledActivityService;

	public EventAdministrationController(EventRepository eventRepository, CinemaHallRepository cinemaHallRepository, CinemaShowRepository cinemaShowRepository) {
		this.eventRepository = eventRepository;
		this.cinemaHallRepository = cinemaHallRepository;
		this.scheduledActivityService = new ScheduledActivityService(eventRepository, cinemaShowRepository);
	}


	@GetMapping(value = "/manage/rooms", params = {"room", "date"})
	@PreAuthorize("hasRole('BOSS')")
	public String getEvents(Model m, @RequestParam("room") Long room,
							  		 @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();
		CinemaHall cinemaHall = cinemaHallRepository.findById(room).orElseThrow(() -> new IllegalArgumentException("Invalid room ID: " + room));
		m.addAttribute("allCinemaHalls", allCinemaHalls);
		m.addAttribute("title","Saalverwaltung");

		List<ScheduledActivity> activitiesOnDate = scheduledActivityService.getActivitysOnDay(date);
		List<ScheduledActivity> scheduledActivitysOnDateInHall = new ArrayList<>();
		
		//übernehme nur die Shows und Events für gesuchten Saal
		for(ScheduledActivity activity : activitiesOnDate){
			if(activity.getCinemaHall().getId().equals(room)) {
				scheduledActivitysOnDateInHall.add(activity);
			}
		}
		m.addAttribute("scheduledActivitysOnDate", scheduledActivitysOnDateInHall);
		m.addAttribute("room", cinemaHall);
		m.addAttribute("date", date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

		return "manage-rooms";
	}


	@GetMapping(value = "/manage/rooms")
	@PreAuthorize("hasRole('BOSS')")
	public String getEventPage(Model m){


		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();
		m.addAttribute("allCinemaHalls", allCinemaHalls);
		m.addAttribute("title","Saalverwaltung");

		return "manage-rooms";
	}


	@PostMapping("/manage/rooms")
	@PreAuthorize("hasRole('BOSS')")
	public String addEvent(RedirectAttributes redirectAttributes,
						   @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
						   @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
						   @RequestParam("room") Long room,
						   @RequestParam("eventname") String eventname){

		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();
		redirectAttributes.addAttribute("allCinemaHalls", allCinemaHalls);
		redirectAttributes.addAttribute("title","Saalverwaltung");
		LocalDate date = from.toLocalDate();

		if(from.isAfter(to)){
			redirectAttributes.addFlashAttribute("errorMessage", "Der Startzeitpunkt muss vor dem Endzeitpunkt liegen.");

			return "redirect:/manage/rooms";
		}else if(from.isBefore(LocalDateTime.now())){
			redirectAttributes.addFlashAttribute("errorMessage", "Das Event darf nicht in der Vergangenheit liegen.");

			return "redirect:/manage/rooms";
		}else if(!scheduledActivityService.isTimeSlotAvailable(from, to, room)){
			redirectAttributes.addFlashAttribute("errorMessage", "Der ausgewählte Zeitslot ist bereits belegt.");

			return "redirect:/manage/rooms?room=" + room + "&date=" + date;
		}


		CinemaHall updateHall = cinemaHallRepository.findById(room).orElseThrow(() -> new IllegalArgumentException("Invalid room ID: " + room));

		int duration = (int) from.until(to, MINUTES);
		Event newEvent = new Event(eventname, from, duration);

		updateHall.addEvent(newEvent);

		cinemaHallRepository.save(updateHall);
		eventRepository.save(newEvent);

		redirectAttributes.addFlashAttribute("successMessage", "Das neue Event wurde erfolgreich angelegt");

		return "redirect:/manage/rooms?room=" + room + "&date=" + date;
	}
}
