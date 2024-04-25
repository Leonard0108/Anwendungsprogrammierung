package kickstart.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.javamoney.moneta.Money;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "CINEMA_SHOWS")
public class CinemaShow implements Comparable<CinemaShow>{
	@Id
	@GeneratedValue
	private Long id;
	private LocalDateTime startDateTime;
	private Money basePrice;

	public CinemaShow(LocalDateTime startDateTime, Money basePrice) {
		this.startDateTime = startDateTime;
		this.basePrice = basePrice;
	}

	public CinemaShow() {}

	public Long getId() {
		return this.id;
	}

	public LocalDateTime getStartDateTime() {
		return this.startDateTime;
	}

	public Money getBasePrice() {
		return this.basePrice;
	}

	@Override
	public int compareTo(CinemaShow cinemaShow) {
		return this.getStartDateTime().compareTo(cinemaShow.getStartDateTime());
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;

		if(object instanceof CinemaShow cinemaShow) {
			return Objects.equals(this.getId(), cinemaShow.getId());
		}
		return false;
	}
}
