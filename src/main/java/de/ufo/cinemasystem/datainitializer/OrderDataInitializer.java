package de.ufo.cinemasystem.datainitializer;

import de.ufo.cinemasystem.models.Orders;
import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.models.Ticket;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.order.OrderManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
@Component
@org.springframework.core.annotation.Order(10)
public class OrderDataInitializer implements DataInitializer{

	private static final Logger LOG = LoggerFactory.getLogger(OrderDataInitializer.class);

	private final OrderManagement<Orders> orderManagement;
	private TicketRepository ticketRepository;
	private SnacksRepository snacksRepository;

	public OrderDataInitializer(OrderManagement orderManagement, TicketRepository ticketRepository, SnacksRepository snacksRepository) {
		Assert.notNull(ticketRepository, "ticketRepository must not be null!");
		Assert.notNull(snacksRepository, "snacksRepository must not be null!");
		Assert.notNull(orderManagement, "Order cant be Zero!");

		this.orderManagement = orderManagement;
		this.ticketRepository = ticketRepository;
		this.snacksRepository = snacksRepository;
	}

	@Override
	public void initialize() {

		if (ticketRepository.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default order entries.");

		Random random = new Random();
		List<Ticket> allTickets = ticketRepository.findAll().toList();
		List<Snacks> allSnacks = snacksRepository.findAll().toList();

		int generatedOrderCount = 40;
		Orders[] allOrders = new Orders[generatedOrderCount];

		//Teilt zufällig Tickets und Snacks auf alle neu generierten Orders auf
		for( int orderIndex = 0; orderIndex < generatedOrderCount; orderIndex++){

			//0-4 Tickets hinzufügen
			for(int i = 0; i < random.nextInt(4); i++){
				//kann eine Order Tickets für verschiedene Vorstellungen enthalten?
				Ticket ticket = allTickets.get(0);
				allOrders[orderIndex].addTicket(ticket);
				allTickets.remove(0);
			}

			//0-3 Snacks hinzufügen
			for(int i = 0; i < random.nextInt(3); i++){
				Snacks snack = allSnacks.get(random.nextInt(allSnacks.size()));
				allOrders[orderIndex].addSnacks(snack);
			}
		}

		//Verteilt alle noch nicht einer Order zugewiesenen Tickets auf erstellte Order auf
		while(!allTickets.isEmpty()){
			allOrders[random.nextInt(generatedOrderCount)].addTicket(allTickets.get(0));
			allTickets.remove(0);
		}

		//Order in DB speichern
		for (Orders order : allOrders) {
			orderManagement.save(order);
		}

	}

}
*/