package de.ufo.cinemasystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.OrderRepository;
import de.ufo.cinemasystem.repository.ReservationRepository;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.repository.UserRepository;

@Controller
public class MakeOrderController {

	private @Autowired OrderRepository orderRepo;
	private @Autowired ReservationRepository reservationRepo;
	private @Autowired SnacksRepository snacksRepository;
	private @Autowired CinemaShowRepository showsRepo;
	private @Autowired UserRepository userRepo;

	@GetMapping("/sell/")
	public String startOrder() {
		return "films-rental-renderer";
	}

}
