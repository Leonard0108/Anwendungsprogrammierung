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

import de.ufo.cinemasystem.models.*;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
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
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.ReservationRepository;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import de.ufo.cinemasystem.repository.UserRepository;
import de.ufo.cinemasystem.services.SnacksService;
import jakarta.servlet.http.HttpSession;


@Controller
@PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
@SessionAttributes("cart")
public class MakeOrderController {

	public static final String orderSessionKey = "current-order";

	private final OrderManagement<Order> orderManagement;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	public MakeOrderController(OrderManagement orderManagement, UniqueInventory<UniqueInventoryItem> inventory) {
		Assert.notNull(orderManagement, "Order cant be Zero!");
		this.orderManagement = orderManagement;
		this.inventory = inventory;
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
	private @Autowired ScheduledActivity.CinemaShowService showService;

	@GetMapping("/sell-tickets")
	public String getMethodName(Model m) {
		m.addAttribute("title", "Kassensystem");
		LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.plusDays(7);
		List<CinemaShow> toOffer = showsRepo.findCinemaShowsInWeek(now.getYear(), AdditionalDateTimeWorker.getWeekOfYear(now)).toList();
		//unhinge any wannabe-unmodifyables by making a copy to a known-writable list type.
		toOffer=new ArrayList<>(toOffer);
        toOffer.addAll(showsRepo.findCinemaShowsInWeek(next.getYear(), AdditionalDateTimeWorker.getWeekOfYear(next)).toList());
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
        m.addAttribute("show", work.getCinemaShow());
        m.addAttribute("price",work.getTotal());
		m.addAttribute("snacks", getAvailableSnacks());

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
		m.addAttribute("snacks", getAvailableSnacks());

		return "sell-items-1";
	}

	@PostMapping("/sell/remove-ticket")
    public String removeTicketFromOrder(Model m, HttpSession session, @RequestParam("deleteCartEntry") ProductIdentifier cartItemId, @ModelAttribute Cart cart){
        if(session.getAttribute(orderSessionKey) == null){
            return "redirect:/sell-tickets";
        }
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		Optional<CartItem> toRemove = cart.getItem(cartItemId.toString());
		if(toRemove.get().getProductName().contains("Ticket")){
			List<Ticket> cartItem = ticketRepo.findByName(toRemove.get().getProductName()).toList();
			this.inventory.delete(inventory.findByProduct(cartItem.get(0)).get());
			cart.removeItem(cartItemId.toString());
			ticketRepo.deleteById(cartItem.get(0).getId());
			showService.update(cartItem.get(0).getCinemaShow().getId()).setSeatOccupancy(new Seat(cartItem.get(0).getSeatID() / 100, cartItem.get(0).getSeatID() % 100), Seat.SeatOccupancy.FREE).save();
		}else{
			cart.removeItem(cartItemId.toString());
		}
		
		
		m.addAttribute("title", "Kassensystem");
        m.addAttribute("show", work.getCinemaShow());
		m.addAttribute("snacks", getAvailableSnacks());
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));

        
        return "sell-items-1";
    }
	
	@PostMapping("/add-reservation")
	public String addTicketsperReservation(Model m, @LoggedIn UserAccount currentUser, HttpSession session,
			@RequestParam("reserveNumber") String reservationId, @ModelAttribute Cart cart) {
		
		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser.getId(), (CinemaShow) m.getAttribute("show")));
		}
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		Ticket[] tickets = reservationRepo.findById(Long.parseLong(reservationId)).get().getTickets();
		for (Ticket ticket : tickets) {
			cart.addOrUpdateItem(ticket, Quantity.of(1));
		}
		m.addAttribute("show", work.getCinemaShow());
		m.addAttribute("snacks", getAvailableSnacks());
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));

		
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
			if(!ticketRepo.existsById(t.getId())){
				ticketRepo.save(t);
				this.inventory.save(new UniqueInventoryItem(t, Quantity.of(1)));
				}
			cart.addOrUpdateItem(t, Quantity.of(1));
			showService.update(work.getCinemaShow()).setSeatOccupancy(
					new Seat(toRowID(spot), Integer.parseInt(spot.substring(1))), Seat.SeatOccupancy.BOUGHT).save();
		}
        m.addAttribute("show", work.getCinemaShow());
		m.addAttribute("snacks", getAvailableSnacks());
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));

		
		return "sell-items-1";

	}


	@PostMapping("/sell/snacks")
	public String addSnacks(Model m, @LoggedIn UserAccount currentUser, 
		HttpSession session, @RequestParam("snack-adder") Snacks snack, @ModelAttribute Cart cart) {
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		
		cart.addOrUpdateItem(snack, Quantity.of(1));
		
		m.addAttribute("show", work.getCinemaShow());
		m.addAttribute("snacks", getAvailableSnacks());
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
		return "sell-items-1";
	}
	

	@PostMapping("/buy")
	public String buy(Model m, @LoggedIn Optional<UserAccount> currentUser, 
		HttpSession session, @ModelAttribute Cart cart) {
		Orders work = (Orders) session.getAttribute(orderSessionKey);

		return currentUser.map(account -> {

			// (｡◕‿◕｡)
			// Mit completeOrder(…) wird der Warenkorb in die Order überführt, diese wird
			// dann bezahlt und abgeschlossen.
			// Orders können nur abgeschlossen werden, wenn diese vorher bezahlt wurden.
			work.setPaymentMethod(Cash.CASH);

			cart.addItemsTo(work);
			sumFinalCartItems(cart,work);
			orderManagement.payOrder(work);
			orderManagement.completeOrder(work);

			cart.clear();
			session.removeAttribute(orderSessionKey);
			return "redirect:/sell-tickets";
		}).orElse("sell-items-1");

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

	private List<CartItem> getCurrentCartTickets(@ModelAttribute Cart cart){
		List<CartItem> cartTickets = new ArrayList<>();
		for (CartItem cartItem : cart.get().toList()) {
			if(cartItem.getProductName().contains("Ticket")){
				cartTickets.add(cartItem);
			}
		}
		return cartTickets;
	}
	private List<CartItem> getCurrentCartSnacks(@ModelAttribute Cart cart){
		List<CartItem>cartSnacks = new ArrayList<>();
		for (CartItem cartItem :  cart.get().toList()) {
			if(cartItem.getProductName().contains("Snack")){
				cartSnacks.add(cartItem);
			}
			
		}
		return cartSnacks;
	}
		
	private void sumFinalCartItems(@ModelAttribute Cart cart, Orders order){
		for (CartItem cartItem :  cart.get().toList()) {
			if(cartItem.getProductName().contains("Snack")){
				order.addSnacks(cartItem.getPrice());
			}else{
				order.addTickets(cartItem.getPrice());
			}
		}
	}

	private List<Snacks> getAvailableSnacks(){
		List<Snacks> SnackstoOffer = this.snacksRepository.findAll().toList();
		SnackstoOffer= new ArrayList<>(SnackstoOffer);
		Iterator<Snacks> snacksiterator = SnackstoOffer.iterator();
		while(snacksiterator.hasNext()){
			Snacks snack= snacksiterator.next();
			if(this.snacksService.getStock(snack.getId()) == 0){
				snacksiterator.remove();
			}
		}
		return SnackstoOffer;
	}

	private static class PatternHolder {

		public static Pattern validSeat = Pattern.compile("[A-La-l]([0-9]|1[0-9])$", Pattern.CASE_INSENSITIVE);
	}


}