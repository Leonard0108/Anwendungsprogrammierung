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

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
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
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Orders;
import de.ufo.cinemasystem.models.Reservation;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.models.Ticket;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.ReservationRepository;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import de.ufo.cinemasystem.services.CinemaShowService;
import de.ufo.cinemasystem.services.SnacksService;
import jakarta.servlet.http.HttpSession;


@Controller
@PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
@SessionAttributes("cart")
public class MakeOrderController {

	public static final String orderSessionKey = "current-order";

	private final OrderManagement<Orders> orderManagement;
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private List<String> errors = new ArrayList<>();

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
	private @Autowired CinemaShowService showService;
	

        @PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
	@GetMapping("/sell-tickets")
	public String onShowSelect(Model m, @ModelAttribute Cart cart) {
		List<String> errors = new ArrayList<>();
		m.addAttribute("title", "Kassensystem");
		LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.plusDays(7);
		List<CinemaShow> toOffer = showsRepo.findCinemaShowsInWeek(now.getYear(), AdditionalDateTimeWorker.getWeekOfYear(now)).toList();
		//unhinge any wannabe-unmodifyables by making a copy to a known-writable list type.
		toOffer = new ArrayList<>(toOffer);
		toOffer = new ArrayList<>(toOffer);
        toOffer.addAll(showsRepo.findCinemaShowsInWeek(next.getYear(), AdditionalDateTimeWorker.getWeekOfYear(next)).toList());
		Iterator<CinemaShow> iterator = toOffer.iterator();
		while(iterator.hasNext()){
			CinemaShow cs= iterator.next();
			if(LocalDateTime.now().until(cs.getStartDateTime(), ChronoUnit.MILLIS) < Duration.ofMinutes(1).toMillis()){
				iterator.remove();
			}
		}
		m.addAttribute("shows", toOffer);
		m.addAttribute("errors", errors);
		
                m.addAttribute("title", "Kassensystem");
		return "sell-items-show-selection";
	}

	@PostMapping("/sell-tickets")
	public String onShowSelectLanding(Model m, @LoggedIn UserAccount currentUser, @ModelAttribute Cart cart,
	@RequestParam("ticket-event") CinemaShow what, HttpSession session) {
		Orders work;
		List<String> errors = new ArrayList<>();
		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser.getId(), what));
			work = (Orders) session.getAttribute(orderSessionKey);
		} else{
			work = (Orders) session.getAttribute(orderSessionKey);
			work.setCinemaShow(what);
		}
		if(!cart.isEmpty()){
			m.addAttribute("cartTickets", getCurrentCartTickets(cart));
			m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
		}
		
		m.addAttribute("title", "Kassensystem");
        m.addAttribute("show", what);
		m.addAttribute("snacks", getAvailableSnacks());
                MakeReservationController.addPricesToModel(m, what);
                m.addAttribute("fskWarning", work.getCinemaShow().getFilm().getFskAge() > 14);
		m.addAttribute("errors", errors);
		return "sell-items-1";
	}

        
        @PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/sell/remove-ticket")
    public String removeTicketFromOrder(Model m, HttpSession session, @RequestParam("deleteCartEntry") ProductIdentifier cartItemId, @ModelAttribute Cart cart){
		List<String> errors = new ArrayList<>();
        if(session.getAttribute(orderSessionKey) == null){
            return "redirect:/sell-tickets";
        }
		List<CartItem> cartList = cart.toList();
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		if(ticketRepo.findById(cartItemId).isPresent()){
			CartItem toRemove = cart.addOrUpdateItem(new Product("Test", Money.of(0, "EUR")), Quantity.of(1)).orElseThrow();
			try {
				for (CartItem cartItem : cartList) {
					if(cartItem.getProductName().equals(ticketRepo.findById(cartItemId).orElseThrow().getName())) {
						toRemove = cartItem;
						break;
					}
					
				}
				if (toRemove.getProductName().equals("Test")){
					errors.add("Kein Ticket zum Löschen gefunden. TicketId: " + cartItemId);
					cart.removeItem(toRemove.getId());
					m.addAttribute("show", work.getCinemaShow());
					m.addAttribute("snacks", getAvailableSnacks());
					m.addAttribute("cartTickets", getCurrentCartTickets(cart));
					m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
					m.addAttribute("errors", errors);
					return "sell-items-1";
				}
				deleteTestItems(cart);
			} catch (Exception e) {
				errors.add("Kein Ticket zum Löschen gefunden. TicketId: " + cartItemId);
				m.addAttribute("show", work.getCinemaShow());
				m.addAttribute("snacks", getAvailableSnacks());
				m.addAttribute("cartTickets", getCurrentCartTickets(cart));
				m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
				m.addAttribute("errors", errors);
				return "sell-items-1";
			}
			Ticket cartItem = ticketRepo.findById(cartItemId).orElseThrow();
			this.inventory.delete(inventory.findByProduct(cartItem).orElseThrow());
			cart.removeItem(toRemove.getId());
			ticketRepo.deleteById(cartItem.getId());
			showService.update(cartItem.getCinemaShow().getId()).setSeatOccupancy(new Seat(cartItem.getSeatID() / 100, cartItem.getSeatID() % 100), Seat.SeatOccupancy.FREE).save();
		} else if(cart.getItem(cartItemId.toString()).isPresent()){
			CartItem toRemove = cart.getItem(cartItemId.toString()).orElseThrow();
			cart.removeItem(cartItemId.toString());
			snacksService.addStock(toRemove.getProduct().getId(), toRemove.getQuantity().getAmount().intValue());
		}else {
			errors.add("Kein Item zum Löschen im Warenkorb gefunden. ItemId: " + cartItemId);
			m.addAttribute("show", work.getCinemaShow());
			m.addAttribute("snacks", getAvailableSnacks());
			m.addAttribute("cartTickets", getCurrentCartTickets(cart));
			m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
			m.addAttribute("errors", errors);
			return "sell-items-1";
		}
				
		m.addAttribute("title", "Kassensystem");
        m.addAttribute("show", work.getCinemaShow());
		m.addAttribute("snacks", getAvailableSnacks());
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
		m.addAttribute("errors", errors);
                m.addAttribute("fskWarning", work.getCinemaShow().getFilm().getFskAge() > 14);
                MakeReservationController.addPricesToModel(m, work.getCinemaShow());
        
        return "sell-items-1";
    }
	

	@GetMapping("/sell-tickets/from-reservation/{reservationId}")
	public String getMethodName(Model m, @PathVariable String reservationId, HttpSession session,
			@LoggedIn UserAccount currentUser, @ModelAttribute Cart cart) {
		Reservation reserve = reservationRepo.findById(Long.parseLong(reservationId)).orElseThrow();
		List<String> errors = new ArrayList<>();
		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser.getId(), reserve.getCinemaShow()));
		}
		if(!cart.isEmpty()){
			m.addAttribute("cartTickets", getCurrentCartTickets(cart));
			m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
		}
		Orders work = (Orders) session.getAttribute(orderSessionKey);

		try {
			addReservationTickets(m, reserve.getId(), work, cart);
		} catch (Exception e) {
		    //TODO: Ist es wirklich nötig, hier Exception abzufangen?
			errors.add(e.getMessage());
			m.addAttribute("error", errors);
		}

		m.addAttribute("title", "Kassensystem");
        m.addAttribute("show", reserve.getCinemaShow());
		m.addAttribute("snacks", getAvailableSnacks());
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
		m.addAttribute("errors", errors);
		
		return "sell-items-1";
	}
	

	@PostMapping("/add-reservation")
	public String addTicketsperReservation(Model m, @LoggedIn UserAccount currentUser, HttpSession session,
			@RequestParam("reserveNumber") String reservationId, @ModelAttribute Cart cart) {
		List<String> errors = new ArrayList<>();
		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser.getId(), (CinemaShow) m.getAttribute("show")));
		}
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		try {
			addReservationTickets(m, Long.parseLong(reservationId), work, cart);
		} catch (Exception e) {
		    //TODO: Ist es wirklich nötig, hier Exception abzufangen?
			errors.add(e.getMessage());
			m.addAttribute("error", errors);
		}
		
		
		m.addAttribute("show", work.getCinemaShow());
		m.addAttribute("snacks", getAvailableSnacks());
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
		m.addAttribute("errors", errors);
                m.addAttribute("title", "Kassensystem");
                MakeReservationController.addPricesToModel(m, work.getCinemaShow());
                m.addAttribute("fskWarning", work.getCinemaShow().getFilm().getFskAge() > 14);

		
		return "sell-items-1";
	}
	
        
        @PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/sell/ticket")
	public String addTickets(Model m, @LoggedIn UserAccount currentUser, HttpSession session,
		@RequestParam("ticketType") String ticketType, @RequestParam("spot") String spot, @ModelAttribute Cart cart) {
		this.errors = new ArrayList<>();

		if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, new Orders(currentUser.getId(), (CinemaShow) m.getAttribute("show")));
		}

		spot = spot.trim().toUpperCase();
		Orders work = (Orders) session.getAttribute(orderSessionKey);

		if (!PatternHolder.validSeat.matcher(spot).matches()) {
			this.errors.add("Ungültiger Sitzplatz: " + spot);
		}
		if (this.errors.isEmpty()
				&& !work.getCinemaShow().containsSeat(toRowID(spot), Integer.parseInt(spot.substring(1)))) {
					this.errors.add("Ungültiger Sitzplatz: " + spot);
		}
		try {
			if (this.errors.isEmpty() && showsRepo.findById(work.getCinemaShow().getId()).orElseThrow()
					.getOccupancy(toRowID(spot), Integer.parseInt(spot.substring(1)))
					.orElseThrow() != Seat.SeatOccupancy.FREE) {
						this.errors.add("Sitzplatz nicht mehr verfügbar: " + spot);
			}
		} catch (NoSuchElementException ex) {
			// CinemaShow no longer exists.
			this.errors.add("Diese Veranstaltung wurde abgesagt");
		}
		
		if (this.errors.isEmpty()) {
			
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
		m.addAttribute("errors", this.errors);
                m.addAttribute("title", "Kassensystem");
                MakeReservationController.addPricesToModel(m, work.getCinemaShow());
                m.addAttribute("fskWarning", work.getCinemaShow().getFilm().getFskAge() > 14);

		
		return "sell-items-1";

	}

        
        @PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/sell/snacks")
	public String addSnacks(Model m, @LoggedIn UserAccount currentUser, 
		HttpSession session, @RequestParam("snack-adder") Snacks snack, @RequestParam("amount") int amount , @ModelAttribute Cart cart) {
		this.errors = new ArrayList<>();
		int check = amount;
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		if(checkSpecificCartSnacks(cart, snack)){
			check += cart.getQuantity(snack).getAmount().intValue();
		}
		if(snacksService.getStock(snack.getId()) >= check){
			cart.addOrUpdateItem(snack, Quantity.of(amount));
			//snacksService.removeStock(snack.getId(), cart.getQuantity(snack).getAmount().intValue());
		}else{
			this.errors.add("Nicht genügend Snacks im Lager!");
		}
		
		
		m.addAttribute("show", work.getCinemaShow());
		m.addAttribute("snacks", getAvailableSnacks());
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
		m.addAttribute("errors", this.errors);
                m.addAttribute("title", "Kassensystem");
                MakeReservationController.addPricesToModel(m, work.getCinemaShow());
                m.addAttribute("fskWarning", work.getCinemaShow().getFilm().getFskAge() > 14);
		return "sell-items-1";
	}
	
        
        @PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
	@PostMapping("/buy")
	public String buy(Model m, @LoggedIn Optional<UserAccount> currentUser, 
		HttpSession session, @ModelAttribute Cart cart) {
		this.errors = new ArrayList<>();
		Orders work = (Orders) session.getAttribute(orderSessionKey);
		
		m.addAttribute("cartTickets", getCurrentCartTickets(cart));
		m.addAttribute("cartSnacks", getCurrentCartSnacks(cart));
                MakeReservationController.addPricesToModel(m, work.getCinemaShow());
                m.addAttribute("title", "Kassensystem");
                m.addAttribute("fskWarning", work.getCinemaShow().getFilm().getFskAge() > 14);

		return currentUser.map(account -> {

			work.setPaymentMethod(Cash.CASH);

			cart.addItemsTo(work);
			sumFinalCartItems(cart,work);
			orderManagement.payOrder(work);
			orderManagement.completeOrder(work);

			cart.clear();
                        session.removeAttribute(orderSessionKey);
			m.addAttribute("show", work.getCinemaShow());
			return "checkout";
		}).orElse("sell-items-1");

	}

        
        @PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
	@GetMapping("/checkout")
	public String checkout(Model m, @LoggedIn Optional<UserAccount> currentUser, 
		HttpSession session, @ModelAttribute Cart cart) {
		return "redirect:/";
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

	private List<Ticket> getCurrentCartTickets(@ModelAttribute Cart cart){
		List<Ticket> cartTickets = new ArrayList<>();
		for (CartItem cartItem : cart.get().toList()) {
			if(cartItem.getProductName().contains("Ticket")){
				cartTickets.add((Ticket)cartItem.getProduct());
			}
		}
		return cartTickets;
	}
	private List<CartItem> getCurrentCartSnacks(@ModelAttribute Cart cart){
		List<CartItem>cartSnacks = new ArrayList<>();
		for (CartItem cartItem :  cart.get().toList()) {
			if(!cartItem.getProductName().contains("Ticket")){cartSnacks.add(cartItem);}
		}
		return cartSnacks;
	}

	private boolean checkSpecificCartSnacks(@ModelAttribute Cart cart, Snacks snack){
		//new ArrayList<>();
		for (CartItem cartItem :  cart.get().toList()) {
			if(cartItem.getProductName().contentEquals(snack.getName()))return true;
		}
		return false;
	}
		
	private void sumFinalCartItems(@ModelAttribute Cart cart, Orders order){
		for (CartItem cartItem :  cart.get().toList()) {
			if(cartItem.getProductName().contains("Ticket")){
				order.addTickets(cartItem.getPrice());
			}else{
				order.addSnacks(cartItem.getPrice());
			}
		}
	}


	private Boolean deleteTestItems(@ModelAttribute Cart cart){
		List<CartItem> toDelete = cart.toList();
		for (CartItem cartItem : toDelete) {
			if(cartItem.getProductName().equals("Test"))cart.removeItem(cartItem.getId());
		}
		return true;
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

	private void addReservationTickets(Model m, Long reservationId, Orders work, @ModelAttribute Cart cart){
		Reservation reserve = reservationRepo.findById(reservationId).orElseThrow();
		if (reserve.getCinemaShow().getId() == work.getCinemaShow().getId()){
			Ticket[] tickets = reserve.getTickets();
			for (Ticket ticket : tickets) {
				cart.addOrUpdateItem(ticket, Quantity.of(1));
				showService.update(work.getCinemaShow()).setSeatOccupancy(
					new Seat(toRowID(ticket.getSeatString()), Integer.parseInt(ticket.getSeatString().substring(1))), Seat.SeatOccupancy.BOUGHT).save();
			}
			reservationRepo.delete(reserve);
		} else {
			m.addAttribute("errors", "Reservierung für einen anderen Film! Bitte Film wechseln oder passende Reservierung nehmen.");
		}
		
	}


	private static class PatternHolder {

		public static Pattern validSeat = Pattern.compile("[A-Za-z]([0-9]|1[0-9])$", Pattern.CASE_INSENSITIVE);
	}


}