package de.ufo.cinemasystem.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ufo.cinemasystem.models.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//benötigte Klassen: Bestellung, (Ausgaben für Personalkosten, Ausgaben für momentan geliehene Filme)
@Controller
public class BusinessDataDashboardController {

	//Anzeigen von Tageseinnahmen Diagramm, darunter Monatsumsatz Diagramm
	@GetMapping("/statistics")
	//@PreAuthorize("hasRole('BOSS')")
	public String getDashboard(Model m){

		List<DailyIncomeData> dailyIncomeData = List.of(
			new DailyIncomeData("Ticket: Bell im Märchenland", 250),
			new DailyIncomeData("Ticket: Parry Hotter und die Hochschule Merseburg", 200),
			new DailyIncomeData("Snacks: Popcorn", 75),
			new DailyIncomeData("Snacks: Softdrinks", 30),
			new DailyIncomeData("Ticket: Extra 4's Jannes Müller", 10)
		);
		m.addAttribute("dailyIncomeData", dailyIncomeData);

		return "business-data-dashboard-boss-renderer";
	}

	public static class DailyIncomeData {
		private String sourceOfIncome;
		private double income;


		public DailyIncomeData(String sourceOfIncome, double income) {
			this.sourceOfIncome = sourceOfIncome;
			this.income = income;
		}

		public List<DailyIncomeData> extractDataFromOrder(Order order){
			//	TODO: Ticketeinnahmen und Snackeinnahmen aus Order in DailyIncomeData List transformieren
			return List.of(new DailyIncomeData("Popcorn", 100.0));
		}

		public String getSource() {
			return sourceOfIncome;
		}

		public double getIncome() {
			return income;
		}
	}
}



