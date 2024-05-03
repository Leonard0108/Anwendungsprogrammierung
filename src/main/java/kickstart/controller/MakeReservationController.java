/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.controller;

import jakarta.servlet.http.HttpSession;
import kickstart.models.CinemaShow;
import kickstart.models.Reservation;
import kickstart.models.UserEntry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Jannik
 */
@Controller
public class MakeReservationController {
    
    public static final String reservationSessionKey = "current-reservation";
    
    /**
     * Entry point from the main nav menu.
     * @param m
     */
    @GetMapping("/reserve-spots/reserve")
    public void startReservation(Model m){}
    
    /**
     * Reservation entry point when the links on current-films are clicked.
     * @param m
     * @param what
     * @param currentUser
     * @param session
     */
    @GetMapping("/reserve-spots/reserve/{what}")
    public void startReservation(Model m, @PathVariable CinemaShow what, @AuthenticationPrincipal UserDetails currentUser, HttpSession session){
        if(session.getAttribute(reservationSessionKey) == null){
            session.setAttribute(reservationSessionKey, new Reservation(new UserEntry(), what));
        }
        Reservation work = (Reservation) session.getAttribute(reservationSessionKey);
    }
    
    /**
     * Form submit of the film selection form.
     * @param m
     * @param what 
     */
    @PostMapping("/reserve-spots/reserve")
    public void onShowSelected(Model m, @RequestParam("event") CinemaShow what, @AuthenticationPrincipal UserDetails currentUser, HttpSession session){
        if(session.getAttribute(reservationSessionKey) == null){
            session.setAttribute(reservationSessionKey, new Reservation(new UserEntry(), what));
        }
        Reservation work = (Reservation) session.getAttribute(reservationSessionKey);
    }
    
    
    /**
     * Form submit of the addTicket form.
     * @param m
     */
    @PostMapping("/reserve-spots/add-ticket")
    public void addTicketToReservation(Model m){}
    
    /**
     * form submit of the commit button.
     */
    @PostMapping("/reserve-spots/commit")
    public void commitReservation(){}
}
