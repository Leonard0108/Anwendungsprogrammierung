package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDERS")
public class Orders extends Order {

    // private @Id @GeneratedValue OrderIdentifier id;
    private Order order;
    private Money TicketSumme;
    private Money SnacksSumme;
    private CinemaShow show;

    @SuppressWarnings({ "unused", "deprecation" })
    private Orders() {
    }

    public Orders(UserAccountIdentifier useraccountidentifier, CinemaShow show) {
        super(useraccountidentifier);
        this.order = new Order(useraccountidentifier);
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

    public Money addSnacks(Snacks snack) {
        order.addOrderLine(snack, Quantity.of(1));
        SnacksSumme.add(snack.getPrice());
        return SnacksSumme;
    }

    public Money addTicket(Ticket ticket) {
        order.addOrderLine(ticket, Quantity.of(1));
        TicketSumme.add(ticket.getPrice());
        return TicketSumme;
    }
}
