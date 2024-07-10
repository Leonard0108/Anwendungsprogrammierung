package de.ufo.cinemasystem.models;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Modellklasse für Tickets
 * @author Jannik Schwaß
 * @author Simon Liepe
 */
@Entity
@Table(name = "TICKETS")
public class Ticket extends Product {

    /**
     * Represents the ticket categories available.
     */
    public static enum TicketCategory {
        /**
         * Everyone who doesn't fall under the below.
         */
        normal,
        /**
         * Reduced price for disabled people.
         */
        reduced,
        /**
         * chhildren (until 14th birthday)
         */
        children
    }

    // private @EmbeddedId ProductIdentifier id =
    // ProductIdentifier.of(UUID.randomUUID().toString());
    private TicketCategory category;
    @ManyToOne
    @JoinColumn(name = "cinema_show_id")
    private CinemaShow show;
    private int seatID;
    

    /**
     * Create a new ticket and apply category reduction.
     * @param Category ticket category
     * @param cinemaShow cinema show
     */
    public Ticket(TicketCategory Category, CinemaShow cinemaShow) {

        super("Ticket", Money.of(0, "EUR"));
        this.category = Category;
        MonetaryAmount reduction;
        switch (this.category) {
            case reduced:
                reduction = Money.of(2, "EUR");
                break;
            case children:
                reduction = Money.of(3, "EUR");
                break;
            default:
                reduction = Money.of(0, "EUR");
        }
        this.show = cinemaShow;

        this.setPrice(show.getBasePrice().subtract(reduction));

    }

    @SuppressWarnings({ "unused", "deprecation" })
    private Ticket() {

    }

    /**
     * get the ticket category.
     * @return ticket category
     */
    public TicketCategory getCategory() {
        return category;
    }

    /**
     * get the cinema show.
     * @return the cinema show
     */
    public CinemaShow getCinemaShow(){
        return show;
    }

    /**
     * Get the film title.
     * @return film title
     */
    public String getTicketShowName() {
        return show.getFilm().getTitle();
    }

    /**
     * Get the raw seat id
     * @return seat id
     */
    public int getSeatID() {
        return seatID;
    }

    /**
     * set the seat id of this ticket and apply place group reduction.
     * @param seatID the seat id
     */
    public void setSeatID(int seatID) {
        this.seatID = seatID;
        this.setName(getName() +" "+ String.valueOf(seatID));
        //deduct seatPlace difference
        Seat.PlaceGroup group = this.show.getCinemaHall().getPlaceGroup(this.seatID / 100, this.seatID % 100).orElseThrow();
        if(group != Seat.PlaceGroup.GROUP_1){
            MonetaryAmount price = this.getPrice();
            switch (group) {
                case GROUP_2:
                    price = price.subtract(Money.of(2, "EUR"));
                    break;
                case GROUP_3:
                    price = price.subtract(Money.of(4, "EUR"));
                    break;
                default:
                    throw new AssertionError("unrecognized group");
            }
            this.setPrice(price);
        }
        
        if(this.getPrice().isLessThan(Money.of(0, "EUR"))){
            //sanity check
            this.setPrice(Money.of(0, "EUR"));
        }
    }

    /**
     * Get the seat spot in string form
     * @return seat spot
     */
    public String getSeatString() {
        return ((char) ('A' + this.seatID / 100)) + ("" + this.seatID % 100);
    }

    /**
     * Returns the UI Label for our ticket category.
     * @return UI Label
     */
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
