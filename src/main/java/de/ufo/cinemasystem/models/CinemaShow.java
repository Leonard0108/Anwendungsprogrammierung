package de.ufo.cinemasystem.models;

import de.ufo.cinemasystem.additionalfiles.ScheduledActivity;
import jakarta.persistence.*;
import org.javamoney.moneta.Money;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "CINEMA_SHOWS")
public class CinemaShow implements Comparable<CinemaShow>, ScheduledActivity {
	@Id
	@GeneratedValue
	private Long id;
	private LocalDateTime startDateTime;
	private Money basePrice;

	/* Chat GPT 3.5
   	Promt: Wie fÃ¼ge ich andere Entity Klassen in eine Entity Klasse als Attribute ein?
 	*/
	// Jede Veranstaltung-Instanz verweist auf eine bestimmte Film-Instanz
	@ManyToOne
	//@JoinColumn(name = "film_id")
	private Film film;

	@ManyToOne
	@JoinColumn(name = "cinema_hall_id")
	private CinemaHall cinemaHall;

	public CinemaShow(LocalDateTime startDateTime, Money basePrice, Film film) {
		this.startDateTime = startDateTime;
		this.basePrice = basePrice;
		this.film = film;
	}

	public CinemaShow() {}

	@Override
	public long getId() {
		return this.id;
	}

	public Film getFilm() {
		return film;
	}

	@Override
	public LocalDateTime getStartDateTime() {
		return this.startDateTime;
	}

	public Money getBasePrice() {
		return this.basePrice;
	}

	@Override
	public CinemaHall getCinemaHall() {
		return this.cinemaHall;
	}

	public void setCinemaHall(CinemaHall cinemaHall) {
		this.cinemaHall = cinemaHall;
	}

	@Override
	public int getDuration(){
		// Kinosaal muss 10 min vor und 10 min nach Filmbeginn gebucht sein
		return film.getTimePlaying() + 20;
	}

	@Override
	public String getName(){
		return film.getTitle();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getStartDateTime(),
                getBasePrice(), getFilm());
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(!(object instanceof CinemaShow cinemaShow))
			return false;

		return Objects.equals(getId(), cinemaShow.getId()) &&
				Objects.equals(getStartDateTime(), cinemaShow.getStartDateTime()) &&
				Objects.equals(getBasePrice(), cinemaShow.getBasePrice()) &&
				Objects.equals(getFilm(), cinemaShow.getFilm());
	}

	@Override
	public int compareTo(CinemaShow cinemaShow) {
		return this.getStartDateTime().compareTo(cinemaShow.getStartDateTime());
	}

	@Override
	public int compareTo(ScheduledActivity scheduledActivity) {
		return this.getStartDateTime().compareTo(scheduledActivity.getStartDateTime());
	}
}
