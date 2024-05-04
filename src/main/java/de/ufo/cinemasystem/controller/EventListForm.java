package de.ufo.cinemasystem.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class EventListForm {
	private String hallName;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfShow;

	public EventListForm(String hallName, LocalDate dateOfShow) {
		this.hallName = hallName;
		this.dateOfShow = dateOfShow;
	}

	public String getHallName() {
		return hallName;
	}

	public LocalDate getDateOfShow() {
		return dateOfShow;
	}
}
