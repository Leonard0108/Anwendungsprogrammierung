/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 *
 * @author Jannik
 */
@Controller
public class ViewProgramController {
    
    /**
     * todo: where rights check?
     * @param week
     * @param m 
     */
    @GetMapping("/current-films/{year}/{week}")
    public String getCurrentProgram(@PathVariable int year, @PathVariable int week , Model m) {

		System.out.println("Start der Woche: " + getStartWeekDateTime(year, week));
		System.out.println("Ende der Woche: " + getEndWeekDateTime(year, week));

		return "welcome";
	}

	// Helper Methoden
	// TODO: Später in einen Service verschieben

	public static int getMaxYearWeeks(int year) {
		return getWeekOfYear(LocalDateTime.of(year, 12, 31, 0, 0));
	}

	public static int getWeekOfYear(LocalDateTime dateTime) {
		return dateTime
			.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
	}

	// Chat GPT 3.5
	// Promt: Wie bekomme ich das Start-Datum zu einer bestimmten Woche?
	public static LocalDateTime getStartWeekDateTime(int year, int week) {
		WeekFields weekFields = WeekFields.of(Locale.getDefault());

		return LocalDateTime.of(year, 1, 1, 0, 0)
			.with(weekFields.weekOfYear(), week)
			.with(weekFields.dayOfWeek(), 1);
	}

	public static LocalDateTime getStartWeekDateTime(LocalDateTime dateTime) {
		return getStartWeekDateTime(
			dateTime.getYear(),
			getWeekOfYear(dateTime)
		);
	}

	public static LocalDateTime getEndWeekDateTime(int year, int week) {
		WeekFields weekFields = WeekFields.of(Locale.getDefault());

		return LocalDateTime.of(year, 1, 1, 23, 59)
			.with(weekFields.weekOfYear(), week)
			.with(weekFields.dayOfWeek(), 7);
	}

	public static LocalDateTime getEndWeekDateTime(LocalDateTime dateTime) {
		return getEndWeekDateTime(
			dateTime.getYear(),
			getWeekOfYear(dateTime)
		);
	}
}
