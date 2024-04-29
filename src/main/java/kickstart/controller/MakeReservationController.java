/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Jannik
 */
@Controller
public class MakeReservationController {
    
    /**
     * 
     * @param m
     */
    @GetMapping("/reserve-spots")
    public void startReservation(Model m){}
    
    /**
     * todo: linking
     * @param cinemaShow
     * @param m 
     */
    @GetMapping("/reserve-spots/{id}")
    public void onCinemaShowSelected(@PathVariable Object cinemaShow, Model m){}
    
    @PostMapping("/reserve-spots/add-ticket")
    public void addTicketToReservation(){}
    
    /**
     * 
     */
    @PostMapping("/reserve-spots/commit")
    public void commitReservation(){}
}
