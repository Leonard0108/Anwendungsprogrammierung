package de.ufo.cinemasystem.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.ufo.cinemasystem.models.Order;

//benötigte Klassen: Bestellung, (Ausgaben für Personalkosten, Ausgaben für momentan geliehene Filme)
@Controller
public class BusinessDataDashboardController {

	//Anzeigen von Tageseinnahmen Diagramm, darunter Monatsumsatz Diagramm
	@GetMapping("/statistics")
	@PreAuthorize("hasRole('BOSS')")
	public String getDashboard(Model m){

		List<FinancialTransaction> dailyIncomeData = List.of(
			new FinancialTransaction("Ticket: Bell im Märchenland", 250),
			new FinancialTransaction("Ticket: Parry Hotter und die Hochschule Merseburg", 200),
			new FinancialTransaction("Snacks: Popcorn", 75),
			new FinancialTransaction("Snacks: Softdrinks", 30),
			new FinancialTransaction("Ticket: Extra 4's Jannes Müller", 10)
		);
		m.addAttribute("dailyIncomeData", dailyIncomeData);

		List<RevenueData> revenueData = List.of(
			new RevenueData(100.0, LocalDate.of(2024, 5, 1)),
			new RevenueData(230.0, LocalDate.of(2024, 5, 2)),
			new RevenueData(170.0, LocalDate.of(2024, 5, 3)),
			new RevenueData(180.0, LocalDate.of(2024, 5, 4)),
			new RevenueData(90.0, LocalDate.of(2024, 5, 5)),
			new RevenueData(110.0, LocalDate.of(2024, 5, 6)),
			new RevenueData(270.0, LocalDate.of(2024, 5, 7)),
			new RevenueData(330.0, LocalDate.of(2024, 5, 8)),
			new RevenueData(400.0, LocalDate.of(2024, 5, 9)),
			new RevenueData(320.0, LocalDate.of(2024, 5, 10)),
			new RevenueData(350.0, LocalDate.of(2024, 5, 11)),
			new RevenueData(300.0, LocalDate.of(2024, 5, 12)),
			new RevenueData(400.0, LocalDate.of(2024, 5, 13)),
			new RevenueData(420.0, LocalDate.of(2024, 5, 14)),
			new RevenueData(470.0, LocalDate.of(2024, 5, 15)),
			new RevenueData(440.0, LocalDate.of(2024, 5, 16)),
			new RevenueData(480.0, LocalDate.of(2024, 5, 17)),
			new RevenueData(520.0, LocalDate.of(2024, 5, 18)),
			new RevenueData(540.0, LocalDate.of(2024, 5, 19)),
			new RevenueData(420.0, LocalDate.of(2024, 5, 20)),
			new RevenueData(550.0, LocalDate.of(2024, 5, 21)),
			new RevenueData(560.0, LocalDate.of(2024, 5, 22)),
			new RevenueData(570.0, LocalDate.of(2024, 5, 23)),
			new RevenueData(600.0, LocalDate.of(2024, 5, 24)),
			new RevenueData(500.0, LocalDate.of(2024, 5, 25)),
			new RevenueData(520.0, LocalDate.of(2024, 5, 26)),
			new RevenueData(510.0, LocalDate.of(2024, 5, 27)),
			new RevenueData(490.0, LocalDate.of(2024, 5, 28)),
			new RevenueData(530.0, LocalDate.of(2024, 5, 29)),
			new RevenueData(550.0, LocalDate.of(2024, 5, 30)),
			new RevenueData(dailyIncomeData, LocalDate.of(2024, 5, 31))
		);
		m.addAttribute("revenueData", revenueData);
                m.addAttribute("title", "Wirtschaftsstatistik");



		return "business-data-dashboard-boss-renderer";
	}

	/**
	 * Repräsentiert eine finanzielle Transaktion im System
	 * und enthält Informationen über die Quelle der Transaktion und den Betrag.
	 * Der Betrag kann positiv für Einnahmen und negativ für Ausgaben sein.
	 */
	public static class FinancialTransaction {
		private String sourceOfTransaction;
		private double amount;

		/**
		 *
		 * @param sourceOfTransaction String, der die Quelle der Transaktion angibt.
		 * @param amount Der Betrag der Transaktion. (positiv für Einnahmen und negativ für Ausgaben)
		 */
		public FinancialTransaction(String sourceOfTransaction, double amount) {
			this.sourceOfTransaction = sourceOfTransaction;
			this.amount = amount;
		}

		/**
		 * Extrahiert die Finanztransaktionsdaten aus einer Order und transformiert sie
		 * in eine Liste von FinancialTransaction Objekten.
		 *
		 * @param orders Liste der Bestellungen, aus denen die Daten extrahiert werden.
		 * @return Eine Liste von FinancialTransaction Objekten.
		 */
		public List<FinancialTransaction> extractDataFromOrders(List<Order> orders){
			//	TODO: Ticketeinnahmen und Snackeinnahmen aus Order in DailyIncomeData List transformieren
			return List.of(new FinancialTransaction("Popcorn", 100.0));
		}

		public String getSource() {
			return sourceOfTransaction;
		}

		public double getAmount() {
			return amount;
		}
	}

	/**
	 * Repräsentiert den  Umsatz des Systems an einem bestimmten Datum.
	 */
	public static class RevenueData {
		private double revenue;
		private LocalDate date;

		/**
		 *
		 * @param transactionsOnDate Liste der Finanztransaktionen an angegebenem Tag.
		 * @param date Das Datum des Umsatzes.
		 */
		public RevenueData(List<FinancialTransaction> transactionsOnDate, LocalDate date){
			this.revenue = 0;
			for(FinancialTransaction transaction : transactionsOnDate){
				this.revenue += transaction.getAmount();
			}
			this.date = date;
		}

		public RevenueData(double revenue, LocalDate date){
			this.revenue = revenue;
			this.date = date;
		}

		public double getRevenue() {
			return revenue;
		}

		public LocalDate getDate() {
			return date;
		}
	}
}



