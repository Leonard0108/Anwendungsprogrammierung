package de.ufo.cinemasystem.models;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Modellklasse für Bestellungen
 * @author Simon Liepe
 */
@Entity
@Table(name = "ORDERS")
public class Orders extends Order{


    private Money ticketSumme;
    private Money SnacksSumme;
    
    @ManyToOne
    @JoinColumn(name = "cinema_show_id")
    private CinemaShow show;

    @SuppressWarnings({ "unused", "deprecation" })
    private Orders() {
    }

    /**
     * Erstelle eine neue Bestellung.
     * @param useraccountidentifier Mitarbeiter-ID
     * @param show Kinovorführung
     */
    public Orders(UserAccountIdentifier useraccountidentifier, CinemaShow show) {
        super(useraccountidentifier);
        this.ticketSumme = Money.of(0, "EUR");
        this.SnacksSumme = Money.of(0, "EUR");
        this.show = show;

    }

    /**
     * Erhalte die Summe aller Tickets.
     * @return Summe aller Tickets.
     */
    public Money getTicketSumme() {
        return ticketSumme;
    }

    /**
     * Erhalte die Summe aller Snacks
     * @return Summe aller Snacks
     */
    public Money getSnacksSumme() {
        return SnacksSumme;
    }

    /**
     * Erhalte die Kinovorführung.
     * @return Kinovorführung
     */
    public CinemaShow getCinemaShow() {
        return show;
    }

    /**
     * Füge Tickets hinzu.
     * @param price Ticket-Preis
     */
    public void addTickets(MonetaryAmount price) {
        this.ticketSumme = this.ticketSumme.add(price);
    }

    /**
     * Füge Snacks hinzu
     * @param price Snack-Preis
     */
    public void addSnacks(MonetaryAmount price) {
        this.SnacksSumme = this.SnacksSumme.add(price);
    }

    /**
     * Ändere die Kinovorführung
     * @param what neue Kinovorführung
     */
    public void setCinemaShow(CinemaShow what) {
        if (what != null) {
            this.show = what;
        }
    }
}
