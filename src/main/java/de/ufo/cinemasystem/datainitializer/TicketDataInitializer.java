package de.ufo.cinemasystem.datainitializer;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Ticket;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Random;

@Component
@Order(10)
public class TicketDataInitializer  implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(TicketDataInitializer.class);
	private TicketRepository ticketRepository;
	private FilmRepository filmRepository;
	private CinemaShowRepository cinemaShowRepository;

	public TicketDataInitializer(TicketRepository ticketRepository, FilmRepository filmRepository, CinemaShowRepository cinemaShowRepository) {
		Assert.notNull(ticketRepository, "ticketRepository must not be null!");
		Assert.notNull(filmRepository, "filmRepository must not be null!");
		Assert.notNull(cinemaShowRepository, "cinemaShowRepository must not be null!");

		this.cinemaShowRepository = cinemaShowRepository;
		this.ticketRepository = ticketRepository;
		this.filmRepository = filmRepository;
	}

	@Override
	public void initialize() {

		if (ticketRepository.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default ticket entries.");

		Random random = new Random();
		for(int i = 0; i < 10; i++) {
            Ticket.TicketCategory[] ticketCategory = new Ticket.TicketCategory[]{
				Ticket.TicketCategory.normal,
				Ticket.TicketCategory.children,
				Ticket.TicketCategory.reduced
			};
			CinemaShow cinemaShow;

			//random TicketCategory und CinemaShow generieren

			Ticket ticket = new Ticket(

			)

		}
	}
}
