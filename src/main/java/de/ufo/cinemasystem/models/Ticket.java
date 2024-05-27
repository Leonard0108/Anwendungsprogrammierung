package de.ufo.cinemasystem.models;

import java.util.Objects;

import javax.money.MonetaryAmount;

import org.salespointframework.catalog.Product;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class Ticket extends Product {

    public static enum TicketCategory {
        normal,
        reduced,
        children
    }

    /**
     * never empty. if null, hibernate will decide one. @SimonBanks42
     */
    private @Id @GeneratedValue Long id;

    private TicketCategory category;
    private CinemaShow show;
    private Reservation reservation;

    Ticket(TicketCategory Category, CinemaShow cinemaShow) {
        this.category = Category;
        double reduction;
        switch (this.category) {
            case reduced:
                reduction = 0.8;
            case children:
                reduction = 0.7;
            default:
                reduction = 1;
        }
        this.show = cinemaShow;
        this.TicketPrice = show.getBasePrice().multiply(reduction);

    }

    public Ticket() {

    }

    public ProductIdentifier getId() {
        return id;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public MonetaryAmount getTicketPrice() {
        return TicketPrice;
    }

    public String getTicketShowName() {
        return show.getFilm().getTitle();
    }

    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public String getSeatString() {
        return ((char) ('A' + this.seatID / 100)) + ("" + this.seatID % 100);
    }

    public String categoryToLabel() {
        return switch (this.category) {
            case normal -> "Erwachsener";
            case children -> "Kind (Bis 14 Jahre)";
            case reduced -> "Schwerbehinderter";
            default -> null;
        };
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (!(object instanceof Ticket ticket))
            return false;

        return Objects.equals(getId(), ticket.getId())
                && Objects.equals(getCategory(), ticket.getCategory())
                && Objects.equals(getTicketPrice(), ticket.getTicketPrice());
    }
    /*
     * @Override
     * public int compareTo(Ticket ticket) {
     * return (this.equals(ticket)) ? 0 : 1;
     * }
     */
}
