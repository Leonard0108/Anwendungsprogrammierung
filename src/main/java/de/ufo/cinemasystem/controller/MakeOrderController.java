package de.ufo.cinemasystem.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.CinemaShowService;
import de.ufo.cinemasystem.models.Orders;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.models.Ticket;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.ReservationRepository;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import de.ufo.cinemasystem.repository.UserRepository;
import jakarta.servlet.http.HttpSession;


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class MakeOrderController {

	public static final String orderSessionKey = "current-order";

	private final OrderManagement<Order> orderManagement;

	public MakeOrderController(OrderManagement orderManagement) {
		Assert.notNull(orderManagement, "Order cant be Zero!");
		this.orderManagement = orderManagement;
	}
	@ModelAttribute("cart")
		Cart initializeCart() {
			return new Cart();
		}

//	private @Autowired OrderRepository orderRepo;
	private @Autowired ReservationRepository reservationRepo;
	private @Autowired SnacksRepository snacksRepository;
	private @Autowired CinemaShowRepository showsRepo;
	private @Autowired TicketRepository ticketRepo;
	private @Autowired UserRepository userRepo;
	private @Autowired CinemaShowService showService;

@GetMapping("/sell/ticket")
public String getMethodName(Model m) {
	m.addAttribute("title", "Kassensystem");
	LocalDateTime now = LocalDateTime.now();
	List<CinemaShow> toOffer = showsRepo.findCinemaShowsInWeek(now.getYear(), AdditionalDateTimeWorker.getWeekOfYear(now)).toList();
	//unhinge any wannabe-unmodifyables by making a copy to a known-writable list type.
	toOffer=new ArrayList<>(toOffer);
	Iterator<CinemaShow> iterator = toOffer.iterator();
	while(iterator.hasNext()){
		CinemaShow cs= iterator.next();
		if(LocalDateTime.now().until(cs.getStartDateTime(), ChronoUnit.MILLIS) < Duration.ofMinutes(30).toMillis()){
			iterator.remove();
		}
	}
	m.addAttribute("shows", toOffer);
    return "sell-items-1";
}


	@GetMapping("/sell-items/{what}")
	public String startOrder(Model m, @AuthenticationPrincipal UserAccountIdentifier currentUser,
		@PathVariable CinemaShow what, HttpSession session) {
		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser, what));
		}
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		m.addAttribute("title", "Kassensystem");
        m.addAttribute("tickets", work.getOrderLines());
        m.addAttribute("show", work.getCinemaShow());
        m.addAttribute("price",work.getTotal());
		return "sell-items-1";
	}

	@PostMapping("/sell/ticket")
	public String addTickets(Model m, @AuthenticationPrincipal UserAccountIdentifier currentUser, HttpSession session,
			@RequestParam("show") CinemaShow show,
			@RequestParam("ticketType") String ticketType, @RequestParam("spot") String spot) {

		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser, show));
		}

		spot = spot.trim().toUpperCase();
		List<String> errors = new ArrayList<>();
		Orders work = (Orders) session.getAttribute(orderSessionKey);

		if (!PatternHolder.validSeat.matcher(spot).matches()) {
			errors.add("Ungültiger Sitzplatz: " + spot);
		}
		if (errors.isEmpty()
				&& !work.getCinemaShow().containsSeat(toRowID(spot), Integer.parseInt(spot.substring(1)))) {
			errors.add("Ungültiger Sitzplatz: " + spot);
		}
		try {
			if (errors.isEmpty() && showsRepo.findById(work.getCinemaShow().getId()).orElseThrow()
					.getOccupancy(toRowID(spot), Integer.parseInt(spot.substring(1)))
					.orElseThrow() != Seat.SeatOccupancy.FREE) {
				errors.add("Sitzplatz nicht mehr verfügbar: " + spot);
			}
		} catch (NoSuchElementException ex) {
			// CinemaShow no longer exists.
			errors.add("Diese Veranstaltung wurde abgesagt");
		}
		
		if (errors.isEmpty()) {
			// add ticket
			Ticket t = new Ticket(toCategoryType(ticketType), show);
			t.setSeatID(100 * toRowID(spot) + Integer.parseInt(spot.substring(1)));
			work.addTickets(ticketRepo.save(t));
			showService.update(work.getCinemaShow()).setSeatOccupancy(
					new Seat(toRowID(spot), Integer.parseInt(spot.substring(1))), Seat.SeatOccupancy.RESERVED).save();
		}

		
		return "sell-items-1";

	}


	@PostMapping("/sell/snacks")
	public String addSnacks(Model m, @AuthenticationPrincipal UserAccountIdentifier currentUser, 
		HttpSession session, @RequestParam("snack") Snacks snack, @ModelAttribute Cart cart) {
		
		cart.addOrUpdateItem(snack, Quantity.of(1));
		
		return "sell-items-1";
	}
	
	private static int toRowID(String spot) {
		char rowChar = spot.charAt(0);
		return rowChar - 'A';
	}

	private static Ticket.TicketCategory toCategoryType(String ticketType) {
		return switch (ticketType) {
			case "adult" -> Ticket.TicketCategory.normal;
			case "child" -> Ticket.TicketCategory.children;
			case "disabled" -> Ticket.TicketCategory.reduced;
			default -> null;
		};
	}

	private static class PatternHolder {

		public static Pattern validSeat = Pattern.compile("[A-La-l]([0-9]|1[0-9])$", Pattern.CASE_INSENSITIVE);
	}

}
