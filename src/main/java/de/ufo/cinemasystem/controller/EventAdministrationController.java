package de.ufo.cinemasystem.controller;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;
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
	private CinemaShowRepository cinemaShowRepository;

	public EventAdministrationController(EventRepository eventRepository, CinemaHallRepository cinemaHallRepository, CinemaShowRepository cinemaShowRepository) {
		this.eventRepository = eventRepository;
		this.cinemaHallRepository = cinemaHallRepository;
		this.cinemaShowRepository = cinemaShowRepository;
	}


	@GetMapping(value = "/manage/rooms", params = "show")
	//@PreAuthorize("hasRole('BOSS')")
	public String getEvents(Model m, @RequestParam("room") Long room,
							  		 @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();
		m.addAttribute("allCinemaHalls", allCinemaHalls);

		List<CinemaShow> showsOnDate = cinemaShowRepository.findCinemaShowsOnDay(date).toList();
		List<CinemaShow> showsOnDateInHall = new ArrayList<>();

		//übernehme nur die Shows für gesuchten Saal
		for(CinemaShow show : showsOnDate){
			if(show.getCinemaHall().getId().equals(room)){
				showsOnDateInHall.add(show);
			}
		}
		m.addAttribute("showsOnDate", showsOnDateInHall);
		m.addAttribute("room", room);
		m.addAttribute("date", date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

		/*
		for(CinemaShow show : showsOnDateInHall){
			System.out.println(show.getFilm().getTitle());
		}
		if(showsOnDateInHall.isEmpty()) System.out.println("no shows");
		*/

		return "manage-rooms-boss-renderer";
	}


	@GetMapping(value = "/manage/rooms")
	//@PreAuthorize("hasRole('BOSS')")
	public String getEventPage(Model m){


		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();
		m.addAttribute("allCinemaHalls", allCinemaHalls);

		return "manage-rooms-boss-renderer-empty";
	}





	@PostMapping("/manage/rooms")
	//@PreAuthorize("hasRole('BOSS')")
	public String addEvent(Model m,
						   @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
						   @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
						   @RequestParam("room") Long room,
						   @RequestParam("eventname") String eventname){

		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();
		m.addAttribute("allCinemaHalls", allCinemaHalls);

		CinemaHall updateHall = cinemaHallRepository.findById(room).get();

		int duration = (int) from.until(to, MINUTES);
		Event newEvent = new Event(eventname, from, duration);

		updateHall.addEvent(newEvent);

		cinemaHallRepository.save(updateHall);
		eventRepository.save(newEvent);

		return "manage-rooms-boss-renderer-success";
	}


}
