package de.ufo.cinemasystem.additionalfiles;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
public class YearWeekEntry implements Comparable<YearWeekEntry>{
	private final int year;
	private final int week;
	@Id
	private Long id;

	public YearWeekEntry(int year, int week) {
		int maxYearWeeks = AdditionalDateTimeWorker.getMaxYearWeeks(year);
		if(week < 1 || week > maxYearWeeks)
			throw new IllegalArgumentException("week muss zwischen 1 und " + maxYearWeeks + " für das Jahr " + year + " liegen (week = " + week + ").");

		this.id = year * 53L + week;
		this.year = year;
		this.week = week;
	}

	protected YearWeekEntry() {
		this.year = 2000;
		this.week = 1;
	}

	public int getYear() {
		return this.year;
	}

	public int getWeek() {
		return this.week;
	}

	public Long getId() { return id; }

	/**
	 * siehe {@link AdditionalDateTimeWorker#getWeekRangeFormat(int, int)}
	 */
	public String getWeekRangeFormat() {
		return AdditionalDateTimeWorker.getWeekRangeFormat(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getEndWeekDateTime(int, int)}
	 */
	public LocalDateTime getEndWeekDateTime() {
		return AdditionalDateTimeWorker.getEndWeekDateTime(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getStartWeekDateTime(int, int)}
	 */
	public LocalDateTime getStartWeekDateTime() {
		return AdditionalDateTimeWorker.getStartWeekDateTime(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#nextWeekEntry(int, int)}
	 */
	public YearWeekEntry nextWeek() {
		return AdditionalDateTimeWorker.nextWeekEntry(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#lastWeekEntry(int, int)}
	 */
	public YearWeekEntry lastWeek() {
		return AdditionalDateTimeWorker.lastWeekEntry(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getMaxYearWeeks(int)}
	 */
	public int getMaxYearWeeks() {
		return AdditionalDateTimeWorker.getMaxYearWeeks(this.year);
	}

	/**
	 * Prüft, ob der angegebene Zeitpunkt in der Woche und in dem Jahr liegt
	 * @param date Zeitpunkt
	 * @return true, wenn Zeitpunkt in der Woche und in dem Jahr liegt, sonst false
	 */
	public boolean isInYearWeek(LocalDateTime date) {
		if(date.getYear() != this.year) return false;
		return AdditionalDateTimeWorker.getWeekOfYear(date) == this.week;
	}

	/**
	 * @return true, wenn der aktuelle Zeitpunkt in der Woche und in dem Jahr liegt, sonst false
	 */
	public boolean isNowInYearWeek() {
		return isInYearWeek(LocalDateTime.now());
	}

	@Override
	public int compareTo(@NotNull YearWeekEntry otherEntry) {
		if(this.year == otherEntry.year)
			return Integer.compare(this.week, otherEntry.week);

		return Integer.compare(this.year, otherEntry.year);
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;
		if(!(object instanceof YearWeekEntry otherEntry))
			return false;
		return (this.year == otherEntry.year && this.week == otherEntry.week);
	}

	@Override
	public int hashCode() {
		return this.year * 53 + this.week;
	}
}
