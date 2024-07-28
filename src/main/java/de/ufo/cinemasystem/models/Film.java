/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ufo.cinemasystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a film.
 *
 * @author Jannik Schwaß
 * @author Yannik Harnisch
 */
@Entity
@Table(name = "FILMS")
public class Film implements Comparable<Film>, PriceChange {

    @Id
    @GeneratedValue
    private Long id;
    private @NotNull
    String title;
    private @NotNull
    String desc;
    private int fskAge;
    //in minutes
    private int timePlaying;

    @ElementCollection(fetch = FetchType.EAGER)
    private final Set<YearWeekEntry> rentWeeks = new TreeSet<>();

    @ManyToOne
    private FilmProvider filmProvider;
    private int basicRentFee;
    private Money basePrice;
    private String imageSource;

    @ElementCollection
    private final List<Double> reducedBasicRentFee = new ArrayList<>();

    /**
     * Creates a new film object, with the specified title, (short) description,
     * timePlaying &amp; FSK age restriction
     *
     * @param title film title
     * @param desc short description
     * @param timePlaying time this film plays in minutes
     * @param fskAge FSK age restriction
     * @param filmProvider provider
     * @throws NullPointerException if title or desc are null
     * @throws IllegalArgumentException if timePlaying &lt;= 0, or fskAge &lt;0
     */
    public Film(String title, String desc, int timePlaying, int fskAge, FilmProvider filmProvider) {
	this.title = title;
	this.desc = desc;
	this.timePlaying = timePlaying;
	this.fskAge = fskAge;
	this.filmProvider = filmProvider;
	this.basePrice = Money.of(-1, "EUR");
	this.basicRentFee = 1000;
    }

    /**
     * Creates a new film object, with the specified title, (short) description,
     * timePlaying &amp; FSK age restriction
     *
     * @param title film title
     * @param desc short description
     * @param timePlaying time this film plays in minutes
     * @param fskAge FSK age restriction
     * @param filmProvider provider
     * @param basicRentFee basis rent price, in € (per Week)
     * @throws NullPointerException if title or desc are null
     * @throws IllegalArgumentException if timePlaying &lt;= 0, or fskAge &lt;0
     */
    public Film(String title, String desc, int timePlaying, int fskAge, FilmProvider filmProvider, int basicRentFee) {
	this.title = title;
	this.desc = desc;
	this.fskAge = fskAge;
	this.timePlaying = timePlaying;
	this.filmProvider = filmProvider;
	this.basicRentFee = basicRentFee;
	this.basePrice = Money.of(-1, "EUR");
    }

    /**
     * Creates a new film object, with the specified title, (short) description,
     * timePlaying &amp; FSK age restriction
     *
     * @param title film title
     * @param desc short description
     * @param timePlaying time this film plays in minutes
     * @param fskAge FSK age restriction
     * @param filmProvider provider
     * @param basicRentFee basis rent price, in € (per Week)
     * @param imageSource Name und Dateiende des Bildes in
     * "/assets/film-posters/"
     * @throws NullPointerException if title or desc are null
     * @throws IllegalArgumentException if timePlaying &lt;= 0, or fskAge &lt;0
     */
    public Film(String title, String desc, int timePlaying, int fskAge, FilmProvider filmProvider, int basicRentFee, String imageSource) {
	this.title = title;
	this.desc = desc;
	this.fskAge = fskAge;
	this.timePlaying = timePlaying;
	this.filmProvider = filmProvider;
	this.basicRentFee = basicRentFee;
	this.basePrice = Money.of(-1, "EUR");
	this.imageSource = imageSource;
    }

    /**
     * Hibernate-only constructor. Do not use, you will break things.
     */
    public Film() {
    }

    /**
     * Get the internal id of this film
     *
     * @return internal id
     */
    public Long getId() {
	return id;
    }

    /**
     * Get the ID string of the film object.
     * @return ID string
     */
    public String getIdString() {
	return "film-" + id.toString();
    }

    /**
     * get the title this film represents.
     *
     * @return title
     */
    public String getTitle() {
	return title;
    }

    /**
     * Anpassung an Snacks für Preisänderungen PriceChange Interface
     *
     * @return film title
     */
    public String getName() {
	return title;
    }

    /**
     * Get a short description of this film
     *
     * @return short description
     */
    public String getDesc() {
	return desc;
    }

    /**
     * get the time this film is playing, in minutes
     *
     * @return time playing
     */
    public int getTimePlaying() {
	return timePlaying;
    }

    /**
     * Get the FSK age restriction of this film, in years
     *
     * @return FSK age restriction
     */
    public int getFskAge() {
	return fskAge;
    }

    /**
     * get the film provider
     * @return film providder
     */
    public FilmProvider getFilmProvider() {
	return filmProvider;
    }

    /**
     * Get the rent fee base price
     * @return fee base price
     */
    public int getBasicRentFee() {
	return basicRentFee;
    }

    /**
     * @param week die Leihwoche für den der Preis berechnet werden soll
     * @return evtl. reduzierten BasicRentFee
     */
    public int getBasicRentFee(int week) {
	return (int) (getReducedBasicRentFee(week) * this.basicRentFee);
    }

	public int getBasicRentFee(YearWeekEntry yearWeekEntry) {
		List<YearWeekEntry> yearWeekEntries = this.getRentWeeks().stream().toList();
		for(int i = 0; i < yearWeekEntries.size(); i++) {
			if(yearWeekEntries.get(i).equals(yearWeekEntry)) {
				return getBasicRentFee(i);
			}
		}
		return getBasicRentFee(yearWeekEntries.size());
	}

    /**
     * set the base rent price
     * @param basicRentFee base rent price
     */
    public void setBasicRentFee(int basicRentFee) {
	this.basicRentFee = basicRentFee;
    }

    /**
     * rent this film
     * @param entry week to rent
     * @return true if anything changed
     */
    public boolean addRentWeek(YearWeekEntry entry) {
	return this.rentWeeks.add(entry);
    }

    /**
     * return this film
     * @param entry week to return
     * @return true if anything changed
     */
    public boolean removeRentWeek(YearWeekEntry entry) {
	return this.rentWeeks.remove(entry);
    }

    /**
     * Get the weeks this film has been rent
     * @return week streamable
     */
    public Streamable<YearWeekEntry> getRentWeeks() {
	return Streamable.of(this.rentWeeks);
    }

    /**
     * get the first week this film was rent, if such a week exist
     * @return optional with week object or null
     */
    public Optional<YearWeekEntry> getFirstRentWeek() {
	return this.rentWeeks.stream().min(Comparator.naturalOrder());
    }

    /**
     * get the last week this film was rent, if such a week exist
     * @return optional with week object or null
     */
    public Optional<YearWeekEntry> getLastRentWeek() {
	return this.rentWeeks.stream().max(Comparator.naturalOrder());
    }

    /**
     * Check wether the film is rent on a date
     * @param dateTime the date
     * @return true if it is
     */
    public boolean isRent(LocalDateTime dateTime) {
	return this.rentWeeks.stream()
		.anyMatch(e -> e.isInYearWeek(dateTime));
    }

    /**
     * Check wether the film is rent in a week
     * @param entry the week
     * @return true if it is
     */
    public boolean isRent(YearWeekEntry entry) {
	return this.rentWeeks.contains(entry);
    }

    /**
     * Get the number of weeks this film has been rented
     * @return count
     */
    public int getRentWeekCount() {
	return this.rentWeeks.size();
    }

    /**
     * Check wether the film is rent right now
     * @return true if it is
     */
    public boolean isRentNow() {
	return isRent(LocalDateTime.now());
    }

    /**
     * Get the image name of the film poster.
     * @return erhalte "Dateiname.Endung als Quelle im Ordner
     * "/assets/film-posters/", wenn kein Bild gesetzt ist erhalte
     * "no_image.png" Vorlage
     */
    public String getImageSource() {
	if (imageSource == null) {
	    return "no-image.png";
	}
	return imageSource;
    }

    /**
     * @param weeksReduction absteigend sortierte Rabat-Werte zwischen 1.0 und
     * 0.0, erster Wert ist für erste Leihwoche, usw. Alle hier nicht gesetzten
     * weiteren Wochen verwenden den letzten Wert.
     *
     */
    public void setReducedBasicRentFee(double... weeksReduction) {
	List<Double> reducedBasicRentFee = new ArrayList<>();
	double max = 1.0;
	for (double weekReduction : weeksReduction) {
	    if (weekReduction < 0.0 || weekReduction > 1.0 || weekReduction > max) {
		throw new IllegalArgumentException("Die Werte müssen absteigend sortiert vorliegen und alle zwischen 0.0 und 1.0 liegen!");
	    }
	    reducedBasicRentFee.add(weekReduction);
	    max = weekReduction;
	}
	this.reducedBasicRentFee.addAll(reducedBasicRentFee);
    }

    /**
     * gibt die prozentuale Reduzierung des Basis-Leihpreis für die n.Woche
       zurück.Alle nicht gesetzten weiteren Wochen verwenden den letzten Wert. Beispiel: 1.0, 1.0, 0.9, 0.8, 0.7 Leihpreis 2. Woche: 1.0 Leihpreis 4.
        Woche: 0.8 Leihpreis 5. Woche: 0.7 Leihpreis 7. Woche: 0.7
     * @param week the week
     * @return reduced rent fee
     */
    public double getReducedBasicRentFee(int week) {
	if (week <= 0) {
	    throw new IllegalArgumentException("week muss größer gleich 1 sein!");
	}
	if (this.reducedBasicRentFee.isEmpty()) {
	    return 1.0;
	}
	if (week <= this.reducedBasicRentFee.size()) {
	    return this.reducedBasicRentFee.get(week - 1);
	}
	return this.reducedBasicRentFee.get(this.reducedBasicRentFee.size() - 1);
    }

    /**
     * base price of a ticket (adult in place group 1)
     * @return base price
     */
    public Money getPrice() {
	return basePrice;
    }

    /**
     * set the base price
     * @param basePrice new base price
     */
    public void setPrice(Money basePrice) {
	this.basePrice = basePrice;
    }

    /**
     * check wether the price has been set
     * @return true if it was
     */
    public boolean isInitialized() {
	return basePrice.getNumber().intValue() != -1;
    }

    /**
     * Gibt an, ob der Film zu dem Zeitpunkt im Kino zu dem Zeitpunkt verfügbar
     * ist (z.B. zum Verwenden in einer Veranstaltung) TODO: auch hier prüfen,
     * ob Ticket-Preise vom Chef gesetzt wurden
     *
     * @param dateTime Zeitpunk
     * @return true, wenn Verfügbar, sonst false
     */
    public boolean isAvailableAt(LocalDateTime dateTime) {
	return isRent(dateTime) && isInitialized();
    }

    /**
     * Gibt an, ob der Film aktuell im Kino verfügbar ist (z.B. zum Verwenden in
     * einer Veranstaltung) TODO: auch hier prüfen, ob Ticket-Preise vom Chef
     * gesetzt wurden
     *
     * @return true, wenn Verfügbar, sonst false
     */
    public boolean isAvailableNow() {
	return isAvailableAt(LocalDateTime.now());
    }

    /**
     * Generate a hash code for this film. Due to the equals contract, hashcode
     * is calculated from the id only.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
	int hash = 3;
	hash = 73 * hash + (int) (this.id ^ (this.id >>> 32));
	return hash;
    }

    /**
     * Checks wether {@code this} and the passed object are identical. Two films
     * are considered identical when they have the same id.
     *
     * @param obj other obj
     * @return true if they are, false otherwise.
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
     * Implements the {@link Comparable} interface for films. Films are sorted
     * according to their titles with the semantics of
     * {@link String#compareTo(java.lang.String) String#compareTo(java.lang.String)}.
     *
     * @param o the other object
     * @return an integer according to compareTo spec
     */
    @Override
    public int compareTo(Film o) {
	if (equals(o)) {
	    return 0;
	}
	return this.title.compareTo(o.title);
    }

    /**
     * Returns a string representation of this object. This method is mainly
     * intended for debugging purposes.
     *
     * @return a string
     */
    @Override
    public String toString() {
	return "Film{" + "title=" + title + ", desc=" + desc + ", fskAge=" + fskAge + ", timePlaying=" + timePlaying + ", basicRentFee=" + basicRentFee + '}';
    }

}
