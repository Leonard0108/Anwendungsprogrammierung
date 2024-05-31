package de.ufo.cinemasystem.controller;


import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import de.ufo.cinemasystem.models.Bestellung;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Ticket;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.OrderRepository;
import de.ufo.cinemasystem.repository.ReservationRepository;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class MakeOrderController {

	public static final String orderSessionKey = "current-reservation";

	private final OrderManagement<Order> orderManagement;

	MakeOrderController(OrderManagement orderManagement){
		Assert.notNull(orderManagement, "Order cant be Zero!");
		this.orderManagement = orderManagement;
	}

	private @Autowired OrderRepository orderRepo;
	private @Autowired ReservationRepository reservationRepo;
	private @Autowired SnacksRepository snacksRepository;
	private @Autowired CinemaShowRepository showsRepo;
	private @Autowired UserRepository userRepo;

	@GetMapping("/sell#tickets")
	public String startOrder(Model m, @PathVariable Ticket ticket, @AuthenticationPrincipal UserAccountIdentifier currentUser, HttpSession session) {

		if(session.getAttribute(orderSessionKey) == null){
            session.setAttribute(orderSessionKey, new Bestellung(currentUser));
        }

		Bestellung bestellung = (Bestellung) session.getAttribute(orderSessionKey);
        m.addAttribute("tickets", bestellung.addTicket(ticket));
		return "sell-items-1";
	}

}
