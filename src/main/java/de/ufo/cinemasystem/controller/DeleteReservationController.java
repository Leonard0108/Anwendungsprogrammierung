
package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.ScheduledActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.ufo.cinemasystem.models.Reservation;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.models.Ticket;
import de.ufo.cinemasystem.repository.ReservationRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import de.ufo.cinemasystem.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring MVC Controller for viewing your own reservations and deleting them.
 * @author Jannik Schwaß
 * @version 1.0
 */
@Controller
public class DeleteReservationController {
    
    private @Autowired ReservationRepository repo;
    private @Autowired TicketRepository ticketRepo;
    private @Autowired ScheduledActivity.CinemaShowService showService;
    private @Autowired UserRepository uRepo;
    
    /**
     * view the reservations of the current user.
     * @param m model
     * @param currentUser logged-in user
     * @return 
     */
    @GetMapping("/my-reservations")
    @PreAuthorize("isAuthenticated()")
    public String getReservations(Model m, @AuthenticationPrincipal UserDetails currentUser, HttpSession session){
        m.addAttribute("title", "Meine Reservierungen");
        m.addAttribute("reservations", repo.findAllByUser(uRepo.findByUserAccountUsername(currentUser.getUsername())));
        if(session.getAttribute("error")!= null){
            m.addAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        return "reservation-list";
    }
    
    /**
     * Post endpoint targeted by the cancel form on the reservation list.
     * @param m
     * @param id
     * @param currentUser
     * @param session
     * @return 
     */
    @PostMapping("/my-reservations/delete/")
    @PreAuthorize("isAuthenticated()")
    public String getDeleteForm2(Model m, @RequestParam(name = "reserveNumber",required = false) Reservation id, @AuthenticationPrincipal UserDetails currentUser, HttpSession session){
        if(id == null){
            session.setAttribute("error", "Reservierung existiert nicht oder gehört nicht ihnen! (ID: " + id + ")");
            return "redirect:/my-reservations";
        }
        if(!id.getReservingAccount().getId().equals(uRepo.findByUserAccountUsername(currentUser.getUsername()).getId())){
            //do NOT leak the reservation context
            session.setAttribute("error", "Reservierung existiert nicht oder gehört nicht ihnen! (ID: " + "null" + ")");
            return "redirect:/my-reservations";
        }
        
        m.addAttribute("title", "Reservierung #"+id.getId());
        m.addAttribute("reservation", id);
        return "cancel-reservation";
    }
    
    /**
     * Get mapping targeted by links in the reservation list.
     * @param m
     * @param id
     * @param currentUser
     * @param session
     * @return 
     */
    @GetMapping("/cancel-reservation/{id}")
    @PreAuthorize("isAuthenticated()")
    public String getDeleteForm(Model m, @PathVariable(name = "id", required = false) Reservation id, @AuthenticationPrincipal UserDetails currentUser, HttpSession session){
        if(id == null){
            session.setAttribute("error", "Reservierung existiert nicht oder gehört nicht ihnen! (ID: " + id + ")");
            return "redirect:/my-reservations";
        }
        if(!id.getReservingAccount().getId().equals(uRepo.findByUserAccountUsername(currentUser.getUsername()).getId())){
            //do NOT leak the reservation context
            session.setAttribute("error", "Reservierung existiert nicht oder gehört nicht ihnen! (ID: " + "null" + ")");
            return "redirect:/my-reservations";
        }
        
        m.addAttribute("title", "Reservierung #"+id.getId());
        m.addAttribute("reservation", id);
        return "cancel-reservation";
    }
    
    /**
     * post mapping to actually delete a reservation.
     * @param m
     * @param id
     * @param currentUser
     * @param session
     * @return 
     */
    @PostMapping("/cancel-reservation/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteReservation(Model m,@PathVariable Reservation id, @AuthenticationPrincipal UserDetails currentUser, HttpSession session){
        if(id == null){
            session.setAttribute("error", "Reservierung existiert nicht oder gehört nicht ihnen! (ID: " + id + ")");
            return "redirect:/my-reservations";
        }
        if(!id.getReservingAccount().getId().equals(uRepo.findByUserAccountUsername(currentUser.getUsername()).getId())){
            //do NOT leak the reservation context
            session.setAttribute("error", "Reservierung existiert nicht oder gehört nicht ihnen! (ID: " + "null" + ")");
            return "redirect:/my-reservations";
        }
        
        deleteTickets(id);
        repo.delete(id);
        return "redirect:/my-reservations";
    }
    
    /**
     * Internal function to remove tickets from a reservation before the reservation is deleted.
     * keep in sync with {@linkplain de.ufo.cinemasystem.controller.MakeReservationController#deleteTickets(de.ufo.cinemasystem.models.Reservation) }
     * @param rev the reservation
     */
    private void deleteTickets(Reservation rev){
        Ticket[] tickets = rev.getTickets();
        for(Ticket t:tickets){
            rev.removeTicket(t);
            rev = repo.save(rev);
            showService.update(rev.getCinemaShow().getId()).setSeatOccupancy(new Seat((int) (t.getSeatID() / 100), (int) (t.getSeatID() % 100)), Seat.SeatOccupancy.FREE).save();
            ticketRepo.delete(t);
        }
    }
}
