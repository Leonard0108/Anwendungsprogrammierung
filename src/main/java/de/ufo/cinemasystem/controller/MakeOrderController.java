package de.ufo.cinemasystem.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
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
import de.ufo.cinemasystem.services.SnacksService;
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
	private @Autowired SnacksService snacksService;
	private @Autowired CinemaShowRepository showsRepo;
	private @Autowired TicketRepository ticketRepo;
	private @Autowired UserRepository userRepo;
	private @Autowired CinemaShowService showService;

	@GetMapping("/sell-tickets")
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

		return "sell-itmes-show-selection";
	}


	@GetMapping("/sell-tickets/{what}")
	public String startOrder(Model m, @LoggedIn UserAccount currentUser,
		@PathVariable CinemaShow what, HttpSession session) {
		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser.getId(), what));
		}
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		m.addAttribute("title", "Kassensystem");
        m.addAttribute("cart", work.getOrderLines());
        m.addAttribute("show", work.getCinemaShow());
        m.addAttribute("price",work.getTotal());
		return "sell-items-1";
	}

	@PostMapping("/sell-tickets")
	public String onShowSelect(Model m, @LoggedIn UserAccount currentUser,
	@RequestParam("ticket-event") CinemaShow what, HttpSession session) {
		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser.getId(), what));
		}
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		m.addAttribute("ticket-event", what);
		m.addAttribute("title", "Kassensystem");
        m.addAttribute("show", what);
		List<Snacks> SnackstoOffer = snacksRepository.findAll().toList();
		SnackstoOffer= new ArrayList<>(SnackstoOffer);
		Iterator<Snacks> snacksiterator = SnackstoOffer.iterator();
		while(snacksiterator.hasNext()){
			Snacks snack= snacksiterator.next();
			if(snacksService.getStock(snack.getId()) == 0){
				snacksiterator.remove();
			}
		}
		m.addAttribute("snack", SnackstoOffer);
		return "sell-items-1";
	}

	@PostMapping("/sell/remove-ticket")
    public String removeTicketFromOrder(Model m, HttpSession session, @RequestParam("deleteCartEntry") ProductIdentifier ticketId, @ModelAttribute Cart cart){
        if(session.getAttribute(orderSessionKey) == null){
            return "redirect:/sell-tickets";
        }
		Optional<CartItem> toRemove = cart.getItem(ticketId.toString());
		Streamable<Ticket> t1 = ticketRepo.findByName(toRemove.get().getProductName());
		List<Ticket> ticket = ticketRepo.findByName(toRemove.get().getProductName()).toList();
		Orders work = (Orders) session.getAttribute(orderSessionKey);
        System.out.println("u: " + session.getAttribute("User"));
        System.out.println("t: " + cart.get());
		cart.removeItem(ticketId.toString());
		ticketRepo.deleteById(ticket.get(0).getId());
        showService.update(ticket.get(0).getCinemaShow().getId()).setSeatOccupancy(new Seat(ticket.get(0).getSeatID() / 100, ticket.get(0).getSeatID() % 100), Seat.SeatOccupancy.FREE).save();
        m.addAttribute("title", "Kassensystem");
        m.addAttribute("tickets", cart.get().toList());
        m.addAttribute("show", ticket.get(0).getCinemaShow());
        m.addAttribute("price",cart.getPrice());
        
        return "sell-items-1";
    }

	@PostMapping("/sell/ticket")
	public String addTickets(Model m, @LoggedIn UserAccount currentUser, HttpSession session,
			@RequestParam("ticketType") String ticketType, @RequestParam("spot") String spot, @ModelAttribute Cart cart) {

		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser.getId(), (CinemaShow) m.getAttribute("show")));
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
			Ticket t = new Ticket(toCategoryType(ticketType), work.getCinemaShow());
			t.setSeatID(100 * toRowID(spot) + Integer.parseInt(spot.substring(1)));
			if(!ticketRepo.existsById(t.getId()))ticketRepo.save(t);
			cart.addOrUpdateItem(t, Quantity.of(1));
			showService.update(work.getCinemaShow()).setSeatOccupancy(
					new Seat(toRowID(spot), Integer.parseInt(spot.substring(1))), Seat.SeatOccupancy.RESERVED).save();
		}
        m.addAttribute("show", work.getCinemaShow());
        m.addAttribute("price",cart.getPrice());

		
		return "sell-items-1";

	}


	@PostMapping("/sell/snacks")
	public String addSnacks(Model m, @LoggedIn UserAccount currentUser, 
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
