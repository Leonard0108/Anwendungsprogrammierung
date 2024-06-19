package de.ufo.cinemasystem.models;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import jakarta.persistence.Id;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public class YearWeekEntry implements Comparable<YearWeekEntry>, Serializable {
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

	public YearWeekEntry(Long id) {
		this( (int)(id - 1) / 53, (int)((id - 1) % 53 + 1));
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
         * @return 
	 */
	public String getWeekRangeFormat() {
		return AdditionalDateTimeWorker.getWeekRangeFormat(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getEndWeekDateTime(int, int)}
         * @return 
	 */
	public LocalDateTime getEndWeekDateTime() {
		return AdditionalDateTimeWorker.getEndWeekDateTime(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getStartWeekDateTime(int, int)}
         * @return 
	 */
	public LocalDateTime getStartWeekDateTime() {
		return AdditionalDateTimeWorker.getStartWeekDateTime(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#nextWeekEntry(int, int)}
         * @return 
	 */
	public YearWeekEntry nextWeek() {
		return AdditionalDateTimeWorker.nextWeekEntry(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#lastWeekEntry(int, int)}
         * @return 
	 */
	public YearWeekEntry lastWeek() {
		return AdditionalDateTimeWorker.lastWeekEntry(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getMaxYearWeeks(int)}
         * @return 
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

	public static YearWeekEntry getNowYearWeek() {
		LocalDateTime now = LocalDateTime.now();
		return new YearWeekEntry(now.getYear(), AdditionalDateTimeWorker.getWeekOfYear(now));
	}
}
