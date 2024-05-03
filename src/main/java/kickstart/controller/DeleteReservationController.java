/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.controller;

import kickstart.models.Reservation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Jannik
 */
public class DeleteReservationController {
    
    @GetMapping("/cancel-reservation")
    @PreAuthorize("USER")
    public void getReservations(Model m){}
    
    @GetMapping("/cancel-reservation/{@id}")
    @PreAuthorize("USER")
    public void getDeleteForm(Model m, Reservation id){}
    
    @PostMapping("/cancel-reservation/{@id}")
    @PreAuthorize("USER")
    public void deleteReservation(@PathVariable Reservation id){}
}
