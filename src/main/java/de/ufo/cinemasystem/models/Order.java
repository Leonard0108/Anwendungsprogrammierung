package de.ufo.cinemasystem.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javamoney.moneta.Money;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class Order {

    private @Id @GeneratedValue Long id;
    private LocalDateTime Datum;
    private List<Ticket> tickets = new ArrayList<>();
    private List<Snacks> snacks = new ArrayList<>();
    private Money TicketSumme;
    private Money SnacksSumme;

    Order() {
        this.Datum = LocalDateTime.now();
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
        snacks.add(snack);
        SnacksSumme.add(snack.getPrice());
        return SnacksSumme;
    }

    public Money addTicket(Ticket ticket) {
        tickets.add(ticket);
        SnacksSumme.add(ticket.getTicketPrice());
        return SnacksSumme;
    }

}
