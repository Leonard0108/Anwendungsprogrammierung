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
 *
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
    private List<Object> tickets;

    /**
     * create a new reservation, initially containing 0 tickets.
     * @param reservingAccountID
     * @param cinemaShowID 
     */
    public Reservation(long reservingAccountID, long cinemaShowID) {
        this.reservingAccountID = reservingAccountID;
        this.cinemaShowID = cinemaShowID;
    }

    public Long getId() {
        return id;
    }

    public long getReservingAccountID() {
        return reservingAccountID;
    }

    public long getCinemaShowID() {
        return cinemaShowID;
    }
    
    
    public void addTicket(Object ticket){
        if(this.tickets.contains(ticket)){
            return;
        }
        this.tickets.add(ticket);
    }
    
    public void removeTicket(Object ticket){
        this.tickets.remove(ticket);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

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
