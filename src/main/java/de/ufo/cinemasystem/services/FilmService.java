package de.ufo.cinemasystem.services;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Orders;
import de.ufo.cinemasystem.models.YearWeekEntry;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import de.ufo.cinemasystem.repository.OrdersRepository;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.util.List;

@Service
public class FilmService {
	@Autowired
	FilmRepository filmRepository;

	@Autowired
	CinemaShowRepository cinemaShowRepository;

	@Autowired
	OrdersRepository ordersRepository;

	/**
	 * Erhalte reduzierten Basis-Preis + 0,3 * Einnahmen aus den Tickets zu dem Film
	 */
	public Money getFullRentFee(Film film, int week) {
		List<YearWeekEntry> yearWeekEntries = film.getRentWeeks().stream().toList();
		if(yearWeekEntries.size() < week) {
			return Money.of(film.getBasicRentFee(week), "EUR");
		}
		YearWeekEntry yearWeekEntry = yearWeekEntries.get(week - 1);
		Money weekTicketSum = Money.of(0, "EUR");
		for(CinemaShow cinemaShow : cinemaShowRepository.findCinemaShowsInWeek(yearWeekEntry.getYear(), yearWeekEntry.getWeek(), film)) {
			for(Orders order : ordersRepository.findOrdersByCinemaShow(cinemaShow)) {
				weekTicketSum = weekTicketSum.add(order.getTicketSumme());
			}
		}
		return Money.of(film.getBasicRentFee(week), "EUR").add(weekTicketSum.multiply(0.3));
	}
}
