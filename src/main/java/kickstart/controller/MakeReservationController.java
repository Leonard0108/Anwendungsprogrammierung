/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.controller;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import kickstart.models.CinemaShow;
import kickstart.models.DummyEntity;
import kickstart.models.Reservation;
import kickstart.models.UserEntry;
import kickstart.repository.CinemaShowRepository;
import kickstart.repository.DummyEntityRepository;
import kickstart.repository.ReservationRepository;
import kickstart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Jannik
 */
@Controller
public class MakeReservationController {
    
    public static final String reservationSessionKey = "current-reservation";
    
    private @Autowired ReservationRepository repo;
    private @Autowired CinemaShowRepository showsRepo;
    private @Autowired UserRepository uRepo;
    private @Autowired DummyEntityRepository dummyRepo;
    
    /**
     * Entry point from the main nav menu.
     * @param m
     * @return 
     */
    @GetMapping("/reserve-spots/reserve")
    public String startReservation(Model m){
        m.addAttribute("title", "Plätze reservieren");
        LocalDateTime now = LocalDateTime.now();
        m.addAttribute("shows", showsRepo.findCinemaShowsInWeek(now.getYear(), ViewProgramController.getWeekOfYear(now)).toList());
        return "make-reservation-cinema-show-selection";
    }
    
    /**
     * Reservation entry point when the links on current-films are clicked.
     * @param m
     * @param what
     * @param currentUser
     * @param session
     */
    @GetMapping("/reserve-spots/reserve/{what}")
    public String startReservation(Model m, @PathVariable CinemaShow what, @AuthenticationPrincipal UserDetails currentUser, HttpSession session){
        if(session.getAttribute(reservationSessionKey) == null){
            session.setAttribute(reservationSessionKey, new Reservation(new UserEntry(), what));
        }
        Reservation work = (Reservation) session.getAttribute(reservationSessionKey);
        m.addAttribute("title", "Plätze reservieren");
        m.addAttribute("tickets", work.getTickets());
        m.addAttribute("show", work.getCinemaShow());
        return "make-reservation-ticket-adder";
    }
    
    /**
     * Form submit of the film selection form.
     * @param m
     * @param what 
     * @param currentUser 
     * @param session 
     */
    @PostMapping("/reserve-spots/reserve")
    public String onShowSelected(Model m, @RequestParam("event") CinemaShow what, @AuthenticationPrincipal UserDetails currentUser, HttpSession session){
        if(session.getAttribute(reservationSessionKey) == null){
            session.setAttribute(reservationSessionKey, new Reservation(new UserEntry(), what));
        }
        Reservation work = (Reservation) session.getAttribute(reservationSessionKey);
        m.addAttribute("title", "Plätze reservieren");
        m.addAttribute("tickets", work.getTickets());
        m.addAttribute("show", work.getCinemaShow());
        return "make-reservation-ticket-adder";
    }
    
    
    /**
     * Form submit of the addTicket form.
     * @param m
     * @param session
     * @param ticketType
     * @return 
     */
    @PostMapping("/reserve-spots/add-ticket")
    public String addTicketToReservation(Model m, HttpSession session, @RequestParam("ticketType") String ticketType, @RequestParam("spot") String spot){
        if(session.getAttribute(reservationSessionKey) == null){
            return "redirect:/reserve-spots/reserve";
        }
        Reservation work = (Reservation) session.getAttribute(reservationSessionKey);
        DummyEntity d = new DummyEntity();
        d.setData(ticketType + "<br>" + spot);
        d=dummyRepo.save(d);
        work.addTicket(d);
        m.addAttribute("title", "Plätze reservieren");
        m.addAttribute("tickets", work.getTickets());
        m.addAttribute("show", work.getCinemaShow());
        return "make-reservation-ticket-adder";
    }
    
    /**
     * Form submit of the removeTicket form.
     * @param m
     * @param session
     * @param ticket
     */
    @PostMapping("/reserve-spots/remove-ticket")
    public String removeTicketFromReservation(Model m, HttpSession session, @RequestParam("deleteCartEntry") DummyEntity ticket){
        if(session.getAttribute(reservationSessionKey) == null){
            return "redirect:/reserve-spots/reserve";
        }
        Reservation work = (Reservation) session.getAttribute(reservationSessionKey);
        work.removeTicket(ticket);
        m.addAttribute("title", "Plätze reservieren");
        m.addAttribute("tickets", work.getTickets());
        m.addAttribute("show", work.getCinemaShow());
        return "make-reservation-ticket-adder";
    }
    
    /**
     * form submit of the commit button.
     * @param redir
     * @param session
     * @return 
     */
    @PostMapping("/reserve-spots/commit")
    public String commitReservation(RedirectAttributes redir, HttpSession session){
        if(session.getAttribute(reservationSessionKey) == null){
            return "redirect:/reserve-spots/reserve";
        }
        Reservation work = (Reservation) session.getAttribute(reservationSessionKey);
        //No ticket? get out of here!
        if(work.getTickets().length == 0){
            return "redirect:/reserve-spots/reserve/" + work.getCinemaShow().getId();
        }
        /**
         * todo: remove debug dummy code
         */
        uRepo.save(work.getReservingAccount());
        redir.addFlashAttribute("ok", "created");
        repo.save(work);
        return "redirect:/my-reservations";
    }
}
