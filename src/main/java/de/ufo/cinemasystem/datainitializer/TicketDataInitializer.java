package de.ufo.cinemasystem.datainitializer;

import de.ufo.cinemasystem.models.*;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.SeatRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;

@Component
@Order(10)
public class TicketDataInitializer  implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(TicketDataInitializer.class);
	private TicketRepository ticketRepository;
	private CinemaShowRepository cinemaShowRepository;
	private CinemaShowService cinemaShowService;
	private SeatRepository seatRepository;

	public TicketDataInitializer(TicketRepository ticketRepository, CinemaShowRepository cinemaShowRepository, CinemaShowService cinemaShowService, SeatRepository seatRepository) {
		Assert.notNull(ticketRepository, "ticketRepository must not be null!");
		Assert.notNull(cinemaShowRepository, "cinemaShowRepository must not be null!");
		Assert.notNull(seatRepository, "seatRepository must not be null!");

		this.cinemaShowRepository = cinemaShowRepository;
		this.ticketRepository = ticketRepository;
		this.cinemaShowService = cinemaShowService;
		this.seatRepository = seatRepository;
	}

	@Override
	public void initialize() {

		if (ticketRepository.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default ticket entries.");

		Random random = new Random();
		List<CinemaShow> allCinemaShow = cinemaShowRepository.findAll().toList();
		int seatRowCount = 12;
		int seatPositionCount = 18;
		for(int i = 0; i < 100; i++) {

            Ticket.TicketCategory[] ticketCategorys = new Ticket.TicketCategory[]{
				Ticket.TicketCategory.normal,
				Ticket.TicketCategory.children,
				Ticket.TicketCategory.reduced,
				Ticket.TicketCategory.normal
			};

			//random TicketCategory und CinemaShow generieren
			Seat seat = seatRepository.findByRowPos(random.nextInt(seatRowCount),random.nextInt(seatPositionCount)).orElseThrow(() -> new IllegalArgumentException("Invalid Seat ID generated in: TicketDataInitializer"));
			CinemaShowService.CinemaShowUpdater cinemaShowUpdater = cinemaShowService.update(allCinemaShow.get(random.nextInt(allCinemaShow.size())));
			cinemaShowUpdater.setSeatOccupancy(seat,
												Seat.SeatOccupancy.BOUGHT);
			CinemaShow cinemaShow = cinemaShowUpdater.save();

			Ticket ticket = new Ticket(
				ticketCategorys[random.nextInt(4)],
				cinemaShow
			);
			ticket.setSeatID(seat.getId());


			ticketRepository.save(ticket);

		}

		ticketRepository.findAll().forEach(t -> {
			System.out.println("Kategorie: " + t.getCategory());
			System.out.println("Vorführung: " + t.getTicketShowName());
			System.out.println("ID: " + t.getId());
			System.out.println("Saal: " + t.getShow().getCinemaHall().getName());
			System.out.println("Sitzplatz: " + t.getSeatID());
			System.out.println("Preis: " + t.getPrice());
			System.out.println("=======================================");
		});
	}
}
