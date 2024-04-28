package kickstart.models;

import jakarta.persistence.*;
import org.javamoney.moneta.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "CINEMA_SHOWS")
public class CinemaShow implements Comparable<CinemaShow>{
	@Id
	@GeneratedValue
	private Long id;
	private LocalDateTime startDateTime;
	private Money basePrice;

	@OneToOne
	private MovieStub movie;

	public CinemaShow(LocalDateTime startDateTime, Money basePrice, MovieStub movie) {
		this.startDateTime = startDateTime;
		this.basePrice = basePrice;
		this.movie = movie;
	}

	public CinemaShow() {}

	public Long getId() {
		return this.id;
	}

	public MovieStub getMovie() {
		return movie;
	}

	public LocalDateTime getStartDateTime() {
		return this.startDateTime;
	}

	public Money getBasePrice() {
		return this.basePrice;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getStartDateTime(),
                getBasePrice(), getMovie());
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(!(object instanceof CinemaShow cinemaShow))
			return false;

		return Objects.equals(getId(), cinemaShow.getId()) &&
				Objects.equals(getStartDateTime(), cinemaShow.getStartDateTime()) &&
				Objects.equals(getBasePrice(), cinemaShow.getBasePrice()) &&
				Objects.equals(getMovie(), cinemaShow.getMovie());
	}

	@Override
	public int compareTo(CinemaShow cinemaShow) {
		return this.getStartDateTime().compareTo(cinemaShow.getStartDateTime());
	}
}
