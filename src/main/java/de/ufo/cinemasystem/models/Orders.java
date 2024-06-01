package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Order")
public class Orders extends Order {

    private @Id @GeneratedValue OrderIdentifier id;
    private Orders order;
    private Money TicketSumme;
    private Money SnacksSumme;

    public Orders(UserAccountIdentifier useraccountidentifier) {
        this.order = new Orders(useraccountidentifier);
        this.TicketSumme = Money.of(0, "EUR");
        this.SnacksSumme = Money.of(0, "EUR");
    }

    public OrderIdentifier getId() {
        return id;
    }

    public Money getTicketSumme() {
        return TicketSumme;
    }

    public Money getSnacksSumme() {
        return SnacksSumme;
    }

    public Money addSnacks(Snacks snack) {
        order.addOrderLine(snack, Quantity.of(1));
        SnacksSumme.add(snack.getPrice());
        return SnacksSumme;
    }

    public Money addTicket(Ticket ticket) {
        order.addOrderLine(ticket, Quantity.of(1));
        TicketSumme.add(ticket.getTicketPrice());
        return TicketSumme;
    }

}
