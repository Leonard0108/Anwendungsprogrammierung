package de.ufo.cinemasystem.additionalfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class AdditionalDateTimeWorker {
	/**
	 * @return Ausgabe String: "Wochentag, Datum (dd.MM.yyyy)"
	 */
	public static String getDayFormat(LocalDate date) {
		// EEEE: Wochentag, Sprache hängt von Locale.getDefault() ab
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", Locale.getDefault());
		return date.format(formatter);
	}

	/**
	 * @param dateTime beliebiges Datum (+ Zeitpunk)
	 *                 siehe {@link #getWeekRangeFormat(int, int)}
	 */
	public static String getWeekRangeFormat(LocalDateTime dateTime) {
		return getWeekRangeFormat(dateTime.getYear(), getWeekOfYear(dateTime));
	}

	/**
	 * @return Ausgabe String: "Woche-Von-Datum (dd.MM.yyyy) - Woche-Bis-Datum
	 *         (dd.MM.yyyy)"
	 */
	public static String getWeekRangeFormat(int year, int week) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());

		String formattedStartDate = getStartWeekDateTime(year, week).format(formatter);
		String formattedEndDate = getEndWeekDateTime(year, week).format(formatter);

		return formattedStartDate + " - " + formattedEndDate;
	}

	/**
	 * @param dateTime beliebiges Datum (+ Zeitpunk)
	 *                 siehe {@link #getEndWeekDateTime(int, int)}
	 */
	public static LocalDateTime getEndWeekDateTime(LocalDateTime dateTime) {
		return getEndWeekDateTime(
				dateTime.getYear(),
				getWeekOfYear(dateTime));
	}

	/**
	 *
	 * @param year das Jahr
	 * @param week die Kalenderwoche zum angegebenen Jahr
	 * @return Enddatum der Woche (Sonntag um 23:59)
	 */
	public static LocalDateTime getEndWeekDateTime(int year, int week) {
		WeekFields weekFields = WeekFields.of(Locale.getDefault());

		return LocalDateTime.of(year, 1, 1, 23, 59)
				.with(weekFields.weekOfYear(), week)
				.with(weekFields.dayOfWeek(), 7);
	}

	/**
	 * @param dateTime beliebiges Datum (+ Zeitpunk)
	 *                 siehe {@link #getStartWeekDateTime(int, int)}
	 */
	public static LocalDateTime getStartWeekDateTime(LocalDateTime dateTime) {
		return getStartWeekDateTime(
				dateTime.getYear(),
				getWeekOfYear(dateTime));
	}

	// Chat GPT 3.5
	// Promt: Wie bekomme ich das Start-Datum zu einer bestimmten Woche?
	/**
	 *
	 * @param year das Jahr
	 * @param week die Kalenderwoche zum angegebenen Jahr
	 * @return Startdatum der Woche (Montag um 00:00)
	 */
	public static LocalDateTime getStartWeekDateTime(int year, int week) {
		WeekFields weekFields = WeekFields.of(Locale.getDefault());

		return LocalDateTime.of(year, 1, 1, 0, 0)
				.with(weekFields.weekOfYear(), week)
				.with(weekFields.dayOfWeek(), 1);
	}

	/**
	 * @return aktuelle Kalenderwoche eines Jahres
	 */
	public static int getWeekOfYear(LocalDateTime dateTime) {
		return dateTime
				.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
	}

	/**
	 * Erhalte letzte Woche anhand von Jahr und aktueller Woche nach ISO 8601
	 * 
	 * @param year aktuelles Jahr
	 * @param week aktuelle Woche (1..52 oder 1..53 je nach Jahr, 1. Woche im Jahr,
	 *             wenn Donnerstag der Woche in diesem Jahr liegt, siehe ISO 8601)
	 * @return [0]: das letzte Jahr, wenn letzte Woche im alten Jahr liegt (siehe
	 *         week), sonst unverändert, [1]: vorherige Woche (1..52 oder 1..53)
	 */
	public static int[] lastWeek(int year, int week) {
		int maxYearWeeks = getMaxYearWeeks(year);
		if (week < 1 || week > maxYearWeeks)
			throw new IllegalArgumentException("week muss zwischen 1 und " + maxYearWeeks + " liegen!");
		return new int[] { (week == 1 ? year - 1 : year), (week == 1 ? getMaxYearWeeks(year - 1) : week - 1) };
	}

	public static YearWeekEntry lastWeekEntry(int year, int week) {
		int[] lastWeek = lastWeek(year, week);
		return new YearWeekEntry(lastWeek[0], lastWeek[1]);
	}

	/**
	 * Erhalte nächste Woche anhand von Jahr und aktueller Woche nach ISO 8601
	 * 
	 * @param year aktuelles Jahr
	 * @param week aktuelle Woche (1..52 oder 1..53 je nach Jahr, 1. Woche im Jahr,
	 *             wenn Donnerstag der Woche in diesem Jahr liegt, siehe ISO 8601)
	 * @return [0]: das nächste Jahr, wenn nächste Woche im neuen Jahr liegt (siehe
	 *         week), sonst unverändert, [1]: nächste Woche (1..52 oder 1..53)
	 */
	public static int[] nextWeek(int year, int week) {
		int maxYearWeeks = getMaxYearWeeks(year);
		if (week < 1 || week > maxYearWeeks)
			throw new IllegalArgumentException("week muss zwischen 1 und " + maxYearWeeks + " liegen!");
		return new int[] { ((week % maxYearWeeks == 0) ? year + 1 : year), (week % maxYearWeeks) + 1 };
	}

	public static YearWeekEntry nextWeekEntry(int year, int week) {
		int[] nextWeek = nextWeek(year, week);
		return new YearWeekEntry(nextWeek[0], nextWeek[1]);
	}

	/**
	 * @return max. Anzahl an Kalenderwochen, welches ein Jahr hat. (nach ISO 8601)
	 */
	public static int getMaxYearWeeks(int year) {
		LocalDate date = LocalDate.of(year, 6, 1);
		return (int) IsoFields.WEEK_OF_WEEK_BASED_YEAR.rangeRefinedBy(date).getMaximum();
	}
}
