package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Order.OrderIdentifier;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount.UserAccountIdentifier;

import de.ufo.cinemasystem.models.Order.Orders;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
 @Entity
    @Table(
    public class Orders extends Order {
         

        private Orders order;
        private Money TicketS
        private Money SnacksSumme;
        

               this.order = new Orders(useraccountidentifier);
        this.TicketSumme = Money.of(0, "EUR");
        this.SnacksSumme = Money.of(0, "EUR");
    }

    
    public OrderIdentifier getId() {
        return id;
    }

    p

        }
            
        p

        }
            
        p

            SnacksSumme.add(snack.getPr
            return SnacksSumme;
        }

        public Money addTicket(Ticket ticket) 
            order.addOrderLine(ticket, Quantity.of(1))
            TicketSumme.add(ticket.getTicketPr
            return TicketSumme;
        }

        
            
            
            
        