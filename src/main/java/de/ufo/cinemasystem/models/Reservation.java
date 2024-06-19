
package de.ufo.cinemasystem.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.javamoney.moneta.Money;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a single reservation in the system. Calling any of the methods of this class with a null argument will result in a NullPointerException.
 * @author Jannik Schwaß
 * @version 1.0
 */
@Entity
@Table(name = "RESERVATIONS")
public class Reservation {

    private @Id @GeneratedValue Long id;
    // private
    @ManyToOne
    private @NotNull UserEntry reservingAccount;

    @ManyToOne
    private @NotNull CinemaShow cinemaShow;

    /**
     * The tickets of this reservation
     */
    @OneToMany
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Hibernate-only constructor. Do not use, you will break things.
     */
    public Reservation() {
    }

    /**
     * Create a new reservation, initially containing 0 tickets.
     * @param reservingAccount the reserving account
     * @param cinemaShow the cinemaShow to reserve for
     */
    public Reservation(UserEntry reservingAccount, CinemaShow cinemaShow) {
        this.reservingAccount = Objects.requireNonNull(reservingAccount);
        this.cinemaShow = Objects.requireNonNull(cinemaShow);
    }

    /**
     * Get the id of this reservation
     * 
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the reserving account
     * @return the reserving account
     */
    public UserEntry getReservingAccount() {
        return reservingAccount;
    }

    /**
     * set the reserving account
     * @param reservingAccount new reserving account
     */
    public void setReservingAccount(UserEntry reservingAccount) {
        this.reservingAccount = Objects.requireNonNull(reservingAccount);
    }

    /**
     * get the CinemaShow
     * @return the CinemaShow
     */
    public CinemaShow getCinemaShow() {
        return cinemaShow;
    }

    /**
     * set the cinema show.
     * @param cinemaShow 
     */
    public void setCinemaShow(CinemaShow cinemaShow) {
        this.cinemaShow = Objects.requireNonNull(cinemaShow);
    }

    /**
     * Allocates a new array containing all the tickets in this reservation. The
     * returned array has exactly as many elements
     * as there are tickets in this reservation. Changes to the returned array will
     * not affect this class or any other already-obtained return value from
     * this class, but modifying the individual tickets in the array will.
     * @return ticket array, potentially zero-length, but never null
     */
    public Ticket[] getTickets() {
        return this.tickets.toArray(Ticket[]::new);
    }

    /**
     * Add a ticket to this reservation.
     * @param ticket the ticket
     */
    public void addTicket(Ticket ticket) {
        if (this.tickets.contains(ticket)) {
            return;
        }
        this.tickets.add(Objects.requireNonNull(ticket));
    }

    /**
     * Remove a ticket from this reservation. This method has no effect if it isn't called with a ticket from this reservation (including {@code null}
     * @param ticket the ticket to remove
     */
    public void removeTicket(Ticket ticket) {
        this.tickets.remove(ticket);
    }
    
    /**
     * Get the total price for all tickets in this reservation.
     * @return total price in €
     */
    public Money getTotalPrice(){
        Money total = Money.of(0, "EUR");
        for(Ticket t:tickets){
            total = total.add(t.getPrice() != null? t.getPrice():total);
        }
        return total;
    }

    /**
     * Generate a hash code for this film. Due to the equals contract, hashcode is
     * calculated from the id only.
     * 
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * Checks wether {@code this} and the passed object are identical.
     * Two reservations are considered identical when they have the same id.
     * 
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reservation other = (Reservation) obj;
        return Objects.equals(this.id, other.id);
    }

}
