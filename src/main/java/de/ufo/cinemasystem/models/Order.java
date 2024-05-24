package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Order")
public class Order {

    private @Id @GeneratedValue Long id;
    private Order order;
    private Money TicketSumme;
    private Money SnacksSumme;

    Order(UserAccountIdentifier useraccountidentifier) {
        this.order = new Order(useraccountidentifier);
        this.TicketSumme = Money.of(0, "EUR");
        this.SnacksSumme = Money.of(0, "EUR");
    }

    public Long getId() {
        return id;
    }

    public Money getTicketSumme() {
        return TicketSumme;
    }

    public Money getSnacksSumme() {
        return SnacksSumme;
    }

    public Money addSnacks(Snacks snack) {
        order.addChargeLine(snack.getPrice(), snack.getName());
        SnacksSumme.add(snack.getPrice());
        return SnacksSumme;
    }

    public Money addTicket(Ticket ticket) {
        order.addChargeLine(ticket.getTicketPrice(), ticket.getTicketShowName());
        TicketSumme.add(ticket.getTicketPrice());
        return TicketSumme;
    }

}
