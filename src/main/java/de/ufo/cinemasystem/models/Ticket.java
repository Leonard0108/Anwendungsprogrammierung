package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TICKETS")
public class Ticket extends Product {

    public static enum TicketCategory {
        normal,
        reduced,
        children
    }

    // private @EmbeddedId ProductIdentifier id =
    // ProductIdentifier.of(UUID.randomUUID().toString());
    private TicketCategory category;
    @OneToOne
    @JoinColumn(name = "cinema_show_id")
    private CinemaShow show;
    private int seatID;
    

    public Ticket(TicketCategory Category, CinemaShow cinemaShow) {

        super("Ticket", Money.of(0, "EUR"));
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

        this.setPrice(show.getBasePrice().multiply(reduction));

    }

    @SuppressWarnings({ "unused", "deprecation" })
    private Ticket() {

    }

    public TicketCategory getCategory() {
        return category;
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

    /*
     * @Override
     * public boolean equals(Object object) {
     * if (this == object)
     * return true;
     * 
     * if (!(object instanceof Ticket ticket))
     * return false;
     * 
     * return Objects.equals(getId(), ticket.getId())
     * && Objects.equals(getCategory(), ticket.getCategory())
     * && Objects.equals(getTicketPrice(), ticket.getTicketPrice());
     * }
     * 
     * @Override
     * public int compareTo(Ticket ticket) {
     * return (this.equals(ticket)) ? 0 : 1;
     * }
     */
}
