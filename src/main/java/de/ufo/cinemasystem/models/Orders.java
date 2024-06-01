package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Order.OrderIdentifier;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity

Table(n
ic cla
s

        priate 
    ate Money SnacksSumme
    
    pressWarnings({ "unused", 

        pub

           
            this.TicketSumm
            this.SnacksSumme = Money.of

        
        public Money getTick
            return TicketSumme;
        }

        public Money getSnacksSumme() {
            return SnacksS
        }

    public Money addSnacks(Snacks snack) {
        order.addOrderLine(snack, Quantity.of(1));
        SnacksSumme.add(snack.getPrice());
        return SnacksSumme;
    }


               orde

            return TicketSumme;
            
        

        

        
            
        

        

    

    

    

      
    

    

    

     