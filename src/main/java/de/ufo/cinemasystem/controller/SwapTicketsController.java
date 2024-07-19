package de.ufo.cinemasystem.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.salespointframework.order.Cart;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Orders;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.ReservationRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import de.ufo.cinemasystem.services.CinemaShowService;
import jakarta.servlet.http.HttpSession;



/**
 * Spring MVC-Controller des Kassensystems.
 * @author Simon Liepe
 */
@Controller
@PreAuthorize("hasAnyRole('BOSS', 'EMPLOYEE', 'AUTHORIZED_EMPLOYEE')")
@SessionAttributes("cart")
public class SwapTicketsController {

    /**
     * Session key der aktuellen Bestellung ({@linkplain de.ufo.cinemasystem.models.Orders}-Objekt)
     */
	public static final String orderSessionKey = "activeOrder";

	private final OrderManagement<Orders> orderManagement;
    private @Autowired ReservationRepository reservationRepo;
	private @Autowired CinemaShowRepository showsRepo;
	private @Autowired TicketRepository ticketRepo;
	private @Autowired CinemaShowService showService;


    /**
     * Erstelle einen neuen Controller mit den angegebenen Abhängigkeiten.
     * @param orderManagement Order Management
     */
    public SwapTicketsController(OrderManagement orderManagement) {
        Assert.notNull(orderManagement, "Order cant be Zero!");
        this.orderManagement = orderManagement;
    }
    //Initialisierung des Model Attributes
    @ModelAttribute("cart")
    Cart initializeCart() {
        return new Cart();
    }

    @GetMapping("/swap-tickets-old")
    public String showSelection(Model m, HttpSession session) {
        List<String> errors = new ArrayList<>();
        //Initializing Date Range for Refundable Ticket Shows
		LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.plusDays(7);
        //Get Show List from Repo for both Ranges
		List<CinemaShow> toOffer = showsRepo.findCinemaShowsInWeek(now.getYear(), AdditionalDateTimeWorker.getWeekOfYear(now)).toList();
		//unhinge any wannabe-unmodifyables by making a copy to a known-writable list type.
		toOffer = new ArrayList<>(toOffer);
        toOffer.addAll(showsRepo.findCinemaShowsInWeek(next.getYear(), AdditionalDateTimeWorker.getWeekOfYear(next)).toList());
		Iterator<CinemaShow> iterator = toOffer.iterator();
        //Remove Shows which have already started
		while(iterator.hasNext()){
			CinemaShow cs= iterator.next();
			if(LocalDateTime.now().until(cs.getStartDateTime(), ChronoUnit.MILLIS) < Duration.ofMinutes(1).toMillis()){
				iterator.remove();
			}
		}
		m.addAttribute("shows", toOffer);
		m.addAttribute("errors", errors);
        return "swap-tickets";
    }
    
    

    @PostMapping("/swap-tickets-old")
    public String setRefundTicket(@RequestParam("old-spot") String oldspot, @RequestParam("ticket-event") CinemaShow show, Model m, 
    HttpSession session, @LoggedIn UserAccount currentUser, @ModelAttribute Cart cart) {
        List<String> errors = new ArrayList<>();
        String ticketName = "Ticket " + String.valueOf(100 * toRowID(oldspot.toUpperCase()) + Integer.parseInt(oldspot.substring(1)));
        Orders work = getOrderbyShow(show, ticketName);
        if (session.getAttribute(orderSessionKey) == null) {
			session.setAttribute(orderSessionKey, work);
		}
        if (work != null) {
            work.isOpen();
        }else{
            errors.add("Keine passende Bestellung zu dem Ticket gefunden!");
            m.addAttribute("errors", errors);

        }
        m.addAttribute("ticketName",ticketName);
        m.addAttribute("show", show);
        m.addAttribute("activeOrder", work);
		m.addAttribute("errors", errors);
        return "swap-tickets";
    }

    @PostMapping("/swap-tickets-new")
    public String setNewTicket(@RequestParam("new-spot") String newspot, Model m, 
    HttpSession session, @LoggedIn UserAccount currentUser, @ModelAttribute Cart cart) {
        List<String> errors = new ArrayList<>();
        Orders work = (Orders) session.getAttribute("activeOrder");
        String ticketNeu = "Ticket " + String.valueOf(100 * toRowID(newspot.toUpperCase()) + Integer.parseInt(newspot.substring(1)));

        m.addAttribute("ticketNeu",ticketNeu);
        m.addAttribute("show", work.getCinemaShow());
        m.addAttribute("activeOrder", work);
		m.addAttribute("errors", errors);
        return "swap-tickets";
    }
    
    private static int toRowID(String spot) {
		char rowChar = spot.charAt(0);
		return rowChar - 'A';
	}

    private Orders getOrderbyShow(CinemaShow what, String ticketName){
        Orders actualOrder = null;;
        List<Orders> showOrders = orderManagement.findBy(OrderStatus.COMPLETED)
        .filter(orders -> what.equals(orders.getCinemaShow()))
        .toList();
        for (Orders work : showOrders) {
            if(!work.getOrderLines().filter(orderLines -> ticketName.equals(orderLines.getProductName())).isEmpty()){
                actualOrder = work;
            }
        }

        return actualOrder;
    }

}


