package de.ufo.cinemasystem.controller;

import jakarta.validation.Valid;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Event;
import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.EventRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	public String getEvents(Model m, @Valid EventListForm eventListForm){

		m.addAttribute("eventListForm", eventListForm);

		List<CinemaHall> allCinemaHalls = cinemaHallRepository.findAll().toList();
		m.addAttribute("allCinemaHalls", allCinemaHalls);

		System.out.println(eventListForm.getDateOfShow().toString());

		List<CinemaShow> showsOnDate = cinemaShowRepository.findCinemaShowsOnDay(eventListForm.getDateOfShow()).toList();
		List<CinemaShow> showsOnDateInHall = new ArrayList<>();

		if(showsOnDate.isEmpty()){
			//keine Vorstellungen an diesem Tag
			//später Saal und Datum anzeigen mit leerer Liste darunter
			return "manage-rooms-boss-renderer-empty";
		}

		//übernehme nur die Shows für gesuchten Saal
		for(CinemaShow show : showsOnDate){
			if(show.getCinemaHall().getName().equals(eventListForm.getHallName())){
				showsOnDateInHall.add(show);
			}
		}
		m.addAttribute("showsOnDate", showsOnDateInHall);



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
	public String addEvent(@Valid Event newEventForm, Errors result){

		return "manage-rooms-boss-renderer";
	}


}
