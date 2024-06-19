
package de.ufo.cinemasystem.tests.model;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Reservation;
import de.ufo.cinemasystem.models.Ticket;
import de.ufo.cinemasystem.models.UserEntry;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author Jannik Schwaß
 * @version 1.0
 */
@SpringBootTest
public class ReservationTests {
    
    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void checkCreation(){
        CinemaShow c = new CinemaShow();
        UserEntry u = new UserEntry();
        
        Assertions.assertThrows(NullPointerException.class, () -> {
            Reservation re = new Reservation(null, null);
            System.out.println("Hi from ");
            System.out.println(re);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            Reservation re = new Reservation(u, null);
            System.out.println("Hi from ");
            System.out.println(re);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            Reservation re = new Reservation(null, c);
            System.out.println("Hi from ");
            System.out.println(re);
        });
        Assertions.assertDoesNotThrow(() -> {
            Reservation re = new Reservation(u, c);
            System.out.println("Hi from ");
            System.out.println(System.identityHashCode(re));
        });
    }
    
    @Test
    void checkGettersAndSetters(){
        CinemaShow c = new CinemaShow();
        UserEntry u = new UserEntry();
        Reservation rev = new Reservation(u, c);
        Assertions.assertEquals(rev.getCinemaShow(),c);
        rev.setCinemaShow(new CinemaShow());
        //Assertions.assertNotEquals(c, rev.getCinemaShow());
        
        Assertions.assertEquals(u, rev.getReservingAccount());
        rev.setReservingAccount(new UserEntry());
        Assertions.assertNotEquals(u, rev.getReservingAccount());
    }
    
    @Test
    @Disabled
    void checkTickets(){
        CinemaShow c = new CinemaShow();
        //c.
        UserEntry u = new UserEntry();
        Reservation rev = new Reservation(u, c);
        Assertions.assertEquals(0,rev.getTickets().length);
        Ticket t = new Ticket(Ticket.TicketCategory.children, c);
    }
    
}
