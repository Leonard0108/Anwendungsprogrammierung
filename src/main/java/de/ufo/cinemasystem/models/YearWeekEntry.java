package de.ufo.cinemasystem.models;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import jakarta.persistence.Id;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model class for years of the week.
 * @author Yannick Harnisch
 */
public class YearWeekEntry implements Comparable<YearWeekEntry>, Serializable {
        /**
         * year
         */
	private final int year;
        /**
         * week of the year
         */
	private final int week;
        /**
         * ID of this entry
         */
	@Id
	private Long id;

        /**
         * Create a new YearWeekEntry.
         * @param year the year
         * @param week the week number
         * @throws IllegalArgumentException if the week number is invalid
         */
	public YearWeekEntry(int year, int week) {
		int maxYearWeeks = AdditionalDateTimeWorker.getMaxYearWeeks(year);
		if(week < 1 || week > maxYearWeeks)
			throw new IllegalArgumentException("week muss zwischen 1 und " + maxYearWeeks + " für das Jahr " + year + " liegen (week = " + week + ").");

		this.id = year * 53L + week;
		this.year = year;
		this.week = week;
	}

        /**
         * Create a new YearWeekEntry.
         * @param id the id
         */
	public YearWeekEntry(Long id) {
		this( (int)(id - 1) / 53, (int)((id - 1) % 53 + 1));
	}

        /**
         * Create a new YearWeekEntry for week 1, 2000
         */
	protected YearWeekEntry() {
		this.year = 2000;
		this.week = 1;
	}

        /**
         * get the year
         * @return the year.
         */
	public int getYear() {
		return this.year;
	}

        /**
         * get the week number
         * @return the week number
         */
	public int getWeek() {
		return this.week;
	}

        /**
         * Get the id
         * @return the id
         */
	public Long getId() { return id; }

	/**
	 * siehe {@link AdditionalDateTimeWorker#getWeekRangeFormat(int, int)}
         * @return Formatted week span
	 */
	public String getWeekRangeFormat() {
		return AdditionalDateTimeWorker.getWeekRangeFormat(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getEndWeekDateTime(int, int)}
         * @return sunday, 23:59 in this week
	 */
	public LocalDateTime getEndWeekDateTime() {
		return AdditionalDateTimeWorker.getEndWeekDateTime(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getStartWeekDateTime(int, int)}
         * @return monday, 00:00 in this week
	 */
	public LocalDateTime getStartWeekDateTime() {
		return AdditionalDateTimeWorker.getStartWeekDateTime(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#nextWeekEntry(int, int)}
         * @return an entry for next week
	 */
	public YearWeekEntry nextWeek() {
		return AdditionalDateTimeWorker.nextWeekEntry(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#lastWeekEntry(int, int)}
         * @return an entry for the previous week
	 */
	public YearWeekEntry lastWeek() {
		return AdditionalDateTimeWorker.lastWeekEntry(this.year, this.week);
	}

	/**
	 * siehe {@link AdditionalDateTimeWorker#getMaxYearWeeks(int)}
         * @return number of weeks in this year
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
         * Prüft, ob diese Woche momentan ist.
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

        /**
         * Get an instance for the current week.
         * @return an instance
         */
	public static YearWeekEntry getNowYearWeek() {
		LocalDateTime now = LocalDateTime.now();
		return new YearWeekEntry(now.getYear(), AdditionalDateTimeWorker.getWeekOfYear(now));
	}
}
