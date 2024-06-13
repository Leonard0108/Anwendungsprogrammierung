package de.ufo.cinemasystem.models;

import javax.money.MonetaryAmount;

import de.ufo.cinemasystem.repository.CinemaHallRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Entity
@Table(name = "Snacks")
public class Snacks extends Product implements PriceChange {


	public static enum SnackType {
		Getränk,
		Essen
	}

	// private @EmbeddedId ProductIdentifier id =
	// ProductIdentifier.of(UUID.randomUUID().toString());
	// private Metric metric;
	private SnackType type;

	@SuppressWarnings({ "unused", "deprecation" })
	private Snacks() {
	}

    public Snacks(String name, MonetaryAmount price) {
        super(name, price);
    }

    public Snacks(String name, MonetaryAmount price, SnackType type) {
        super(name, price);
        this.type = type;
    }

	public String getSnackType() {
		return this.type.toString();
	}

	public String getIdString(){
		return "snack-" + super.getId().toString();
	}

	@Override
	public void setPrice(Money newPrice) {
		super.setPrice(newPrice);
	}

	@Override
	public Money getPrice(){
		return (Money) super.getPrice();
	}

	public boolean isInitialized(){
		return super.getPrice().getNumber().intValue() != -1;
	}

    @Service
    public static class CinemaHallService {

        @Autowired
        private SeatService seatService;

        @Autowired
        private CinemaHallRepository cinemaHallRepository;

        /**
         * Erstellt einen neuen Kinosaal und speichert diesen in der Datenbank
         * @param name Name des Kinosaals (z.B. "Kinosaal 1")
         * @param seats alle Seats müssen selbst erstellt und einer Platzgruppe zugeordnet werden
         * @return neues Kinosaal-Objekt
         */
        public CinemaHall createCinemaHall(String name, final Map<Seat, Seat.PlaceGroup> seats) {
            CinemaHall cinemaHall = new CinemaHall(name, seats);
            cinemaHallRepository.save(cinemaHall);
            return cinemaHall;
        }

        /**
         * Bessere und einfachere Erstellungsmethode für Kinosäle, in Bezug auf Sitzplätze
         * siehe {@link #createCinemaHall(String, Map)}
         * @param seatRaws Einträge beginnend mit der obersten ersten Reihe (Entry0 -> Reihe 0, Entry1 -> Reihe 1, ...)
         *                 Ein Eintrag (welcher eine Reihe repräsentiert) beinhaltet an erster Stelle
         *                 die Anzahl der Plätze in der Reihe und an zweiter Stelle die Platzgruppe in der Reihe
         */
        @SafeVarargs
        public final CinemaHall createCinemaHall(String name, Map.Entry<Integer, Seat.PlaceGroup>... seatRaws) {
            Map<Seat, Seat.PlaceGroup> seats = new TreeMap<>();
            int rowCounter = 0;
            for(var seatRaw : seatRaws) {
                for(int i = 0; i < seatRaw.getKey(); i++) {
                    seats.put(
                        seatService.getSeatOrCreate(rowCounter, i),
                        seatRaw.getValue()
                    );
                }
                rowCounter++;
            }

            return createCinemaHall(name, seats);
        }
    }
}
