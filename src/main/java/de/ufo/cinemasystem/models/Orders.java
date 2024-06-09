package de.ufo.cinemasystem.models;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDERS")
public class Orders extends Order{


    private Money TicketSumme;
    private Money SnacksSumme;
    
    @ManyToOne
    @JoinColumn(name = "cinema_show_id")
    private CinemaShow show;

    @SuppressWarnings({ "unused", "deprecation" })
    private Orders() {
    }

    public Orders(UserAccountIdentifier useraccountidentifier, CinemaShow show) {
        super(useraccountidentifier);
        this.TicketSumme = Money.of(0, "EUR");
        this.SnacksSumme = Money.of(0, "EUR");
        this.show = show;

    }

    public Money getTicketSumme() {
        return TicketSumme;
    }

    public Money getSnacksSumme() {
        return SnacksSumme;
    }

    public CinemaShow getCinemaShow() {
        return show;
    }

    public void addTickets(MonetaryAmount price) {
        TicketSumme.add(price);
    }

    public void addSnacks(MonetaryAmount price) {
        SnacksSumme.add(price);
    }
}
