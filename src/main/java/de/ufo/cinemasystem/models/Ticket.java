package de.ufo.cinemasystem.models;

import java.util.Objects;
import java.util.UUID;

import javax.money.MonetaryAmount;

import org.salespointframework.catalog.Product;
import org.springframework.lang.NonNull;

import jakarta.persistence.EmbeddedId;
import lombok.Getter;

public class Ticket extends Product {

    public static enum TicketCategory {
        normal,
        reduced,
        children
    }

    private @EmbeddedId ProductIdentifier id = ProductIdentifier.of(UUID.randomUUID().toString());
    private @NonNull @Getter String name;
    private @NonNull @Getter MonetaryAmount TicketPrice;
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

    Ticket() {

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
