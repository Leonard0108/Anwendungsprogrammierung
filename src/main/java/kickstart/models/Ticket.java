package kickstart.models;

import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;

public class Ticket implements Comparable<Ticket> {

    public static enum TicketKategorie {
        normal,
        reduced,
        children
    }

    /*
     * Ticket(TicketKategorie Kategorie, CinemaShow show) {
     * this.kategorie = Kategorie;
     * double reduction;
     * switch (Kategorie) {
     * case reduced:
     * reduction = 0.8;
     * case children:
     * reduction = 0.7;
     * default:
     * reduction = 1;
     * }
     * ;
     * this.TicketPreis = show.getBasePrice() * reduction;
     * }
     */
    Ticket() {

    }

    @NotEmpty
    private @Id @GeneratedValue Long id;

    private TicketKategorie kategorie;

    private double TicketPreis;

    public Long getId() {
        return this.id;
    }

    public TicketKategorie getKategorie() {
        return this.kategorie;
    }

    public double getTicketPreis() {
        return this.TicketPreis;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (!(object instanceof Ticket ticket))
            return false;

        return Objects.equals(getId(), ticket.getId())
                && Objects.equals(getKategorie(), ticket.getKategorie());
    }

    @Override
    public int compareTo(Ticket ticket) {
        return (this.getId().equals(ticket.getId())) ? 0 : 1;
    }

}
