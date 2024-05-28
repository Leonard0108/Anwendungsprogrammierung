package de.ufo.cinemasystem.additionalfiles;

import org.jetbrains.annotations.NotNull;
import org.thymeleaf.dialect.AbstractDialect;

import java.time.LocalDateTime;

public class YearWeekEntry implements Comparable<YearWeekEntry>{
	private final int year;
	private final int week;

	public YearWeekEntry(int year, int week) {
		int maxYearWeeks = AdditionalDateTimeWorker.getMaxYearWeeks(year);
		if(week < 1 || week > maxYearWeeks)
			throw new IllegalArgumentException("week muss zwischen 1 und " + maxYearWeeks + " für das Jahr " + year + " liegen (week = " + week + ").");
		this.year = year;
		this.week = week;
	}

	public int getYear() {
		return this.year;
	}

	public int getWeek() {
		return this.week;
	}

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
