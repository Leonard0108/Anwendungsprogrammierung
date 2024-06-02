package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;import rg.simport jakarta.persistence.Entity;
import jakarta.persistence.Table; 
@Entity
@Table(name = "ORDERS")
public class Orders extends Order {

    // private @Id @GeneratedValue OrderIdentifier id;
    private Order order;
    pri     priate Money SnacksSumme;
    pr
iate CinemaShow show;

@S sW s({ "unused", "deprecation" })
    pri    } 
    public Orders(UserAccountIdentifier useraccountidentifier, CinemaShow show) {
        super(useraccountidentifier);
        this.order = new Order(useraccountidentifier);
        this.TicketSumme = Money.of(0, "EUR");
        this.SnacksSumme = Mon       t his.show = show;
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

    p

           Tick

        }} 

    

    

    



     