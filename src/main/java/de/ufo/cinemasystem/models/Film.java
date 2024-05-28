/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ufo.cinemasystem.models;

import de.ufo.cinemasystem.additionalfiles.YearWeekEntry;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.util.Streamable;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a film.
 * @author Jannik Schwaß
 */
@Entity
@Table(name= "FILMS")
public class Film  implements Comparable<Film>{

	@Id
	@GeneratedValue
    private Long id;
    private @NotNull String title;
    private @NotNull String desc;
    private int fskAge;
    //in minutes
    private int timePlaying;;

	@ElementCollection
	private final Set<YearWeekEntry> rentWeeks = new TreeSet<>();

	@ManyToOne
	private FilmProvider filmProvider;
    private int basicRentFee = -1;

    /**
     * Creates a new film object, with the specified title, (short) description, timePlaying &amp; FSK age restriction
     * @param title
     * @param desc
     * @param timePlaying time this film plays in minutes
     * @param fskAge
     * @throws NullPointerException if title or desc are null
     * @throws IllegalArgumentException if timePlaying &lt;= 0, or fskAge &lt;0
     */
    public Film(String title, String desc, int timePlaying, int fskAge, FilmProvider filmProvider) {
        this.title = title;
        this.desc = desc;
        this.timePlaying = timePlaying;
        this.fskAge = fskAge;
		this.filmProvider = filmProvider;
    }

    public Film(String title, String desc, int timePlaying, int fskAge, FilmProvider filmProvider, int basicRentFee) {
        this.title = title;
        this.desc = desc;
        this.fskAge = fskAge;
        this.timePlaying = timePlaying;
		this.filmProvider = filmProvider;
        this.basicRentFee = basicRentFee;
    }
    
    

    /**
     * Hibernate-only constructor. Do not use, you will break things.
     */
    public Film() {}

	/**
     * Get the internal id of this film
     * @return 
     */
    public Long getId() {
        return id;
    }

    /**
     * get the title this film represents.
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get a short description of this film
     * @return 
     */
    public String getDesc() {
        return desc;
    }

    /**
     * get the time this film is playing, in minutes
     * @return 
     */
    public int getTimePlaying() {
        return timePlaying;
    }

    /**
     * Get the FSK age restriction of this film, in years
     * @return 
     */
    public int getFskAge() {
        return fskAge;
    }

	public FilmProvider getFilmProvider() {
		return filmProvider;
	}

    public int getBasicRentFee() {
        return basicRentFee;
    }

    public void setBasicRentFee(int basicRentFee) {
        this.basicRentFee = basicRentFee;
    }
    
    public boolean addRentWeek(YearWeekEntry entry) {
		return this.rentWeeks.add(entry);
	}

	public boolean removeRentWeek(YearWeekEntry entry) {
		return this.rentWeeks.remove(entry);
	}

	public Streamable<YearWeekEntry> getRentWeeks() {
		return Streamable.of(this.rentWeeks);
	}

	public Optional<YearWeekEntry> getFirstRentWeek() {
		return this.rentWeeks.stream().min(Comparator.naturalOrder());
	}

	public Optional<YearWeekEntry> getLastRentWeek() {
		return this.rentWeeks.stream().max(Comparator.naturalOrder());
	}

	public boolean isRent(LocalDateTime dateTime) {
		return this.rentWeeks.stream()
			.anyMatch(e -> e.isInYearWeek(dateTime));
	}

	public boolean isRent(YearWeekEntry entry) {
		return this.rentWeeks.contains(entry);
	}

	public int getRentWeekCount() {
		return this.rentWeeks.size();
	}

	public boolean isRentNow() {
		return isRent(LocalDateTime.now());
	}

	/**
	 * Gibt an, ob der Film zu dem Zeitpunkt im Kino zu dem Zeitpunkt verfügbar ist (z.B. zum Verwenden in einer Veranstaltung)
	 * TODO: auch hier prüfen, ob Ticket-Preise vom Chef gesetzt wurden
	 * @param dateTime Zeitpunk
	 * @return true, wenn Verfügbar, sonst false
	 */
	public boolean isAvailableAt(LocalDateTime dateTime) {
		return isRent(dateTime);
	}

	/**
	 * Gibt an, ob der Film aktuell im Kino verfügbar ist (z.B. zum Verwenden in einer Veranstaltung)
	 * TODO: auch hier prüfen, ob Ticket-Preise vom Chef gesetzt wurden
	 * @return true, wenn Verfügbar, sonst false
	 */
	public boolean isAvailableNow() {
		return isAvailableAt(LocalDateTime.now());
	}

    /**
     * Generate a hash code for this film. Due to the equals contract, hashcode is calculated from the id only.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * Checks wether {@code this} and the passed object are identical. 
     * Two films are considered identical when they have the same id.
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Film other = (Film) obj;
        return Objects.equals(this.id, other.id);
    }

    
    /**
     * Implements the {@link Comparable} interface for films. Films are sorted according to their titles with the semantics of
     * {@link String#compareTo(java.lang.String) String#compareTo(java.lang.String)}.
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Film o) {
        if(equals(o)){
            return 0;
        }
        return this.title.compareTo(o.title);
    }

    /**
     * Returns a string representation of this object. This method is mainly intended for debugging purposes.
     * @return a string
     */
    @Override
    public String toString() {
        return "Film{" + "title=" + title + ", desc=" + desc + ", fskAge=" + fskAge + ", timePlaying=" + timePlaying + ", basicRentFee=" + basicRentFee + '}';
    }
    
    
    
}
