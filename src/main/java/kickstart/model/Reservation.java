/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single reservation in the system
 * @author Jannik
 */
@Entity
@Table(name= "RESERVATIONS")
public class Reservation {
    
    private @Id @GeneratedValue Long id;
    //private 
    private long reservingAccountID;
    
    private long cinemaShowID;
    
    /**
     * TODO: linking
     */
    @OneToMany
    private List<DummyEntity> tickets;

    /**
     * Create a new reservation, initially containing 0 tickets.
     * @param reservingAccountID
     * @param cinemaShowID 
     */
    public Reservation(long reservingAccountID, long cinemaShowID) {
        this.reservingAccountID = reservingAccountID;
        this.cinemaShowID = cinemaShowID;
    }

    /**
     * Get the id of this reservation
     * @return 
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the account id of whoever reserved this.
     * @return 
     */
    public long getReservingAccountID() {
        return reservingAccountID;
    }

    /**
     * Get the CinemaShowID of the cinema show this reservation belongs to.
     * @return 
     */
    public long getCinemaShowID() {
        return cinemaShowID;
    }
    
    /**
     * Add a ticket to this reservation.
     * @param ticket 
     */
    public void addTicket(DummyEntity ticket){
        if(this.tickets.contains(ticket)){
            return;
        }
        this.tickets.add(ticket);
    }
    
    /**
     * Remove a ticket from this reservation.
     * @param ticket 
     */
    public void removeTicket(DummyEntity ticket){
        this.tickets.remove(ticket);
    }

    
    /**
     * Generate a hash code for this film. Due to the equals contract, hashcode is calculated from the id only.
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
