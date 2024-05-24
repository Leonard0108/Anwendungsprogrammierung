package de.ufo.cinemasystem.models;

import java.util.Objects;

import org.javamoney.moneta.Money;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;

public class Ticket implements Comparable<Ticket> {

    public static enum TicketCategory {
        normal,
        reduced,
        children
    }

    @NotEmpty
    private @Id @GeneratedValue Long id;

    private TicketCategory category;
    private Money TicketPrice;
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

    Ticket() {

    }

    public Long getId() {
        return id;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public Money getTicketPrice() {
        return TicketPrice;
    }

    public String getTicketShowName() {
        return show.getFilm().getTitle();
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

    @Override
    public int compareTo(Ticket ticket) {
        return (this.equals(ticket)) ? 0 : 1;
    }

}
