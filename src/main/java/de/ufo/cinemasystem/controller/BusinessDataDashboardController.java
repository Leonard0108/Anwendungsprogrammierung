package de.ufo.cinemasystem.controller;

import de.ufo.cinemasystem.models.*;
import de.ufo.cinemasystem.repository.*;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;


import javax.money.Monetary;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


//benötigte Klassen: Bestellung, (Ausgaben für Personalkosten, Ausgaben für momentan geliehene Filme)
@Controller
public class BusinessDataDashboardController {

	@Autowired
	FilmRepository filmRepository;

	@Autowired
	TicketRepository ticketRepository;

	@Autowired
	SnacksRepository snacksRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	private final OrderManagement<Order> orderManagement;


	public BusinessDataDashboardController(OrderManagement<Order> orderManagement) {
		Assert.notNull(orderManagement, "Order darf nicht Null sein!");
		this.orderManagement = orderManagement;
	}


	//Anzeigen von Tageseinnahmen Diagramm, darunter Monatsumsatz Diagramm
	@GetMapping("/statistics")
	@PreAuthorize("hasRole('BOSS')")
	public String getDashboard(Model m) {

		List<FinancialTransaction> dailyIncomeData = new ArrayList<>();
		List<RevenueData> revenueData = new ArrayList<>();
		List<Order> allOrders = orderManagement.findBy(Interval.from(LocalDate.now().atStartOfDay()).to(LocalDate.now().atTime(23, 59))).toList();
		for (Order order : allOrders) {

			if (order.getOrderStatus() == OrderStatus.COMPLETED) {

				List<OrderLine> orderLines = order.getOrderLines().get().toList();
				for (OrderLine orderLine : orderLines) {

					Product.ProductIdentifier productId = orderLine.getProductIdentifier();

					// Product kann Ticket oder Snack sein
					Optional<Ticket> ticketOptional = ticketRepository.findById(productId);
					Optional<Snacks> snackOptional = snacksRepository.findById(productId);

					if (ticketOptional.isPresent()) {

						Ticket ticket = ticketOptional.get();
						dailyIncomeData.add(new FinancialTransaction("Ticket: " + ticket.getTicketShowName(), ticket.getPrice().with(Monetary.getDefaultRounding()).getNumber().doubleValue()));

					} else if (snackOptional.isPresent()) {

						Snacks snack = snackOptional.get();
						dailyIncomeData.add(new FinancialTransaction("Snack: " + snack.getName(), snack.getPrice().with(Monetary.getDefaultRounding())	.getNumber().doubleValue()));

					} else {
						throw new IllegalArgumentException("Invalid ProductIdentifier: " + productId);
					}
				}
			}
		}

		// einzelne Snacks und Tickets aus den Ordern bekommen


		for (LocalDate day = LocalDate.now().minusDays(30); !day.isAfter(LocalDate.now()); day = day.plusDays(1)) {

			// Ausgaben berechnen
			Money dailyExpenses = Money.of(0, "EUR");

			//Mitarbeiter Kosten
			for (EmployeeEntry employee : employeeRepository.findAll()) {
				Money employeeCost = employee.getSalary().multiply(7.0 / employee.getHoursPerWeek());
				dailyExpenses = dailyExpenses.add(employeeCost);

			}

			//FilmLeih Kosten
			for (Film film : filmRepository.findAll()) {
				if (film.isRent(day.atStartOfDay())) {
					Money filmRentCost = Money.of(film.getBasicRentFee() / 7.0, "EUR");
					dailyExpenses = dailyExpenses.add(filmRentCost);
				}
			}

			//Einnahmen
			Money dailyIncome = Money.of(0, "EUR");
			Interval wholeDay = Interval.from(day.atStartOfDay()).to(day.atTime(23, 59));
			List<Order> ordersAtDay = orderManagement.findBy(wholeDay).toList();

			for (Order order : ordersAtDay) {
				dailyIncome = dailyIncome.add(order.getTotal());
			}

			Money dailyRevenue = dailyIncome.subtract(dailyExpenses);

			revenueData.add(new RevenueData(dailyRevenue.getNumber().doubleValueExact(), day));
		}

		m.addAttribute("dailyIncomeData", dailyIncomeData);
		m.addAttribute("revenueData", revenueData);
		m.addAttribute("title", "Wirtschaftsstatistik");


		return "business-data-dashboard";
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
		 * @param sourceOfTransaction String, der die Quelle der Transaktion angibt.
		 * @param amount              Der Betrag der Transaktion. (positiv für Einnahmen und negativ für Ausgaben)
		 */
		public FinancialTransaction(String sourceOfTransaction, double amount) {
			this.sourceOfTransaction = sourceOfTransaction;
			this.amount = amount;
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

		public RevenueData(double revenue, LocalDate date) {
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



