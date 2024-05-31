/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Seat;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    
    
    @GetMapping(value = "/include/spots-view/{which}", produces = "image/svg+xml")
    public String getSpotsView(@PathVariable CinemaShow which, Model m, HttpServletResponse rep){
        Streamable<Map.Entry<Seat, Seat.SeatOccupancy>> seatsAndOccupancy = which.getSeatsAndOccupancy();
        List<Map.Entry<Seat, Seat.SeatOccupancy>> seatsList = seatsAndOccupancy.toList();
        Iterator<Map.Entry<Seat, Seat.SeatOccupancy>> iterator = seatsList.iterator();
        List<Integer> cols = new ArrayList<>();
        List<RowId> rows = new ArrayList<>();
        List<SeatWithOccupancy> seats = new ArrayList<>();
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
            seats.add(new SeatWithOccupancy(next.getKey().getPosition(), next.getKey().getRow(),
                    next.getValue(),
                    which.getCinemaHall().getPlaceGroup(next.getKey()).get()));
        }
        //now build cols + rows
        for(int i = 0; i <= maxCols; i++){
            cols.add(i);
        }
        for(int i = 0; i <= maxRows; i++){
            rows.add(new RowId(i));
        }
        //        seat offset + row count              + padding
        int legendOffset = 45 + 15 * (rows.size()) + 50;
        int width =20 + 15 * cols.size() + 30;
        int height = legendOffset + 40;
        //enforce minimums
        height = Math.max(height, 130);
        width = Math.max(width, 190);
        
        m.addAttribute("seats", seats);
        m.addAttribute("width", width);
        m.addAttribute("height", height);
        m.addAttribute("cols", cols);
        m.addAttribute("rows", rows);
        m.addAttribute("legendOffset", legendOffset);
        rep.setContentType("image/svg+xml; charset=UTF-8");
        
        return "image/spots-view";
    }
    
    /**
     * Turn a row id into a row letter.
     * @param row 0-25
     * @return row letter
     */
    public static String getRowLetter(int row){
        return "" + ((char) (1*row + 'A'));
    }
    
    
    private static class SeatWithOccupancy{

        /**
         * 
         * @param x
         * @param y row
         * @param occupancy the occupancy
         */
        public SeatWithOccupancy(int x, int y, Seat.SeatOccupancy occupancy, Seat.PlaceGroup placeGroup) {
            this.x = x;
            this.y = y;
            this.occupancy = Objects.requireNonNull(occupancy);
            this.placeGroup = Objects.requireNonNull(placeGroup);
        }
        
        private final int x;
        private final int y;
        private final Seat.SeatOccupancy occupancy;
        private final Seat.PlaceGroup placeGroup;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Seat.SeatOccupancy getOccupancy() {
            return occupancy;
        }

        
        public String occupancyToColor(){
            switch (occupancy) {
                case FREE -> {
                    return "#b5e61d";
                }
                case RESERVED -> {
                    return "#ffc90e";
                }
                case BOUGHT -> {
                    return "#ed1c24";
                }
                default -> throw new AssertionError("seat with bad occupancy: "+occupancy);
            }
        }

        public Seat.PlaceGroup getPlaceGroup() {
            return placeGroup;
        }
        
        public String placeGroupColor(){
            switch (placeGroup) {
                case GROUP_1 -> {
                    return "#00ffff";
                }
                case GROUP_2 -> {
                    return "#ff00ff";
                }
                case GROUP_3 -> {
                    return "#ffff00";
                }
                default -> throw new AssertionError("seat with bad occupancy: "+occupancy);
            }
        }

        @Override
        public String toString() {
            return "SeatWithOccupancy{" + "x=" + x + ", y=" + y + ", occupancy=" + occupancy + ", placeGroup=" + placeGroup + '}';
        }
        
        
    }
    
    private static class RowId{

        public RowId(int id) {
            this.id = id;
            this.letter = SpotsViewController.getRowLetter(id);
        }
        
        private int id;
        private String letter;

        public int getId() {
            return id;
        }

        public String getLetter() {
            return letter;
        }
        
        
    }
}
