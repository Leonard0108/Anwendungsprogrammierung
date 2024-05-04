/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ufo.cinemasystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.ufo.cinemasystem.models.Reservation;
import de.ufo.cinemasystem.repository.ReservationRepository;

/**
 *
 * @author Jannik
 */
@Controller
public class DeleteReservationController {
    
    private @Autowired ReservationRepository repo;
    
    @GetMapping("/my-reservations")
    //@PreAuthorize("USER")
    public String getReservations(Model m){
        m.addAttribute("title", "Meine Reservierungen");
        m.addAttribute("reservations", repo.findAll());
        return "reservation-list";
    }
    
    @PostMapping("/my-reservations/delete/")
    //@PreAuthorize("USER")
    public String getDeleteForm2(Model m, @RequestParam("reserveNumber") Reservation id){
        return "cancel-reservation";
    }
    
    @GetMapping("/cancel-reservation/{id}")
    //@PreAuthorize("USER")
    public String getDeleteForm(Model m, @PathVariable Reservation id){
        return "cancel-reservation";
    }
    
    @PostMapping("/cancel-reservation/{id}")
    //@PreAuthorize("USER")
    public String deleteReservation(@PathVariable Reservation id){
        return "redirect:/my-reservations";
    }
}
