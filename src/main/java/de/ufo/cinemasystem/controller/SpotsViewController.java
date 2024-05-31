/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Jannik
 */
@Controller
public class SpotsViewController {
    
    private @Autowired CinemaShowRepository showRepo;
    
    @GetMapping(value = "/include/spots-view/{which}", produces = "image/svg+xml")
    public String getSpotsView(@PathVariable CinemaShow which, Model m){
        Streamable<Map.Entry<Seat, Seat.SeatOccupancy>> seatsAndOccupancy = which.getSeatsAndOccupancy();
        List<Map.Entry<Seat, Seat.SeatOccupancy>> seatsList = seatsAndOccupancy.toList();
        Iterator<Map.Entry<Seat, Seat.SeatOccupancy>> iterator = seatsList.iterator();
        List<Integer> cols = new ArrayList<>();
        List<String> rows = new ArrayList<>();
        int maxCols = 0;
        int maxRows = 0;
        //TODO: seats
        while(iterator.hasNext()){
            Map.Entry<Seat, Seat.SeatOccupancy> next = iterator.next();
            if(maxCols < next.getKey().getPosition()){
                maxCols = next.getKey().getPosition();
            }
            if(maxRows <  next.getKey().getRow()){
                maxRows = next.getKey().getRow();
            }
            //TODO: build seats structure
        }
        //now build cols + rows
        for(int i = 0; i <= maxCols; i++){
            cols.add(i);
        }
        for(int i = 0; i <= maxRows; i++){
            rows.add("" + ((char) ('A' + i) ));
        }
        
        m.addAttribute("seats", new String[]{"foo"});
        m.addAttribute("width", "512");
        m.addAttribute("height", "512");
        m.addAttribute("cols", cols);
        m.addAttribute("rows", rows);
        return "spots-view";
    }
}
