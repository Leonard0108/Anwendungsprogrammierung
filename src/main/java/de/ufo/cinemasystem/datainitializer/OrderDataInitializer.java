package de.ufo.cinemasystem.datainitializer;

import de.ufo.cinemasystem.models.*;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.repository.TicketRepository;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;


@Component
@org.springframework.core.annotation.Order(11)
public class OrderDataInitializer implements DataInitializer{

	private static final Logger LOG = LoggerFactory.getLogger(OrderDataInitializer.class);

	private final OrderManagement<Orders> orderManagement;
	private final UserAccountManagement userAccountManagement;
	private TicketRepository ticketRepository;
	private SnacksRepository snacksRepository;

	@Autowired
	private CinemaShowRepository cinemaShowRepository;

	public OrderDataInitializer(OrderManagement orderManagement, UserAccountManagement userAccountManagement, TicketRepository ticketRepository, SnacksRepository snacksRepository) {

        Assert.notNull(ticketRepository, "ticketRepository must not be null!");
		Assert.notNull(snacksRepository, "snacksRepository must not be null!");
		Assert.notNull(orderManagement, "Orders cant be Zero!");
		Assert.notNull(userAccountManagement, "Users cant be Zero!");

		this.orderManagement = orderManagement;
		this.userAccountManagement = userAccountManagement;
		this.ticketRepository = ticketRepository;
		this.snacksRepository = snacksRepository;
	}

	@Override
	public void initialize() {

		if (orderManagement.findAll(Pageable.ofSize(1)).iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default order entries.");

		Random random = new Random();
		List<Ticket> allTickets = ticketRepository.findAll().toList();
		List<Snacks> allSnacks = snacksRepository.findAll().toList();
		List<CinemaShow> allShows = cinemaShowRepository.findAll().toList();
		UserAccount.UserAccountIdentifier userId = userAccountManagement.findAll().toList().get(3).getId();

		int generatedOrderCount = allShows.size();
		Orders[] allOrders = new Orders[generatedOrderCount];

		//Orders[] initialisieren: jede Order hat eine unterschiedliche CinemaShow
		int j = 0;
		for(CinemaShow show : allShows){
			allOrders[j] = new Orders(userId,show);
				j++;
		}

		//Teilt zufällig Tickets und Snacks auf alle neu generierten Orders auf
		for( int orderIndex = 0; orderIndex < generatedOrderCount; orderIndex++){

			//fügt alle Tickets mit gleicher CinemaShow wie Order hinzu
			for(Ticket ticket : allTickets){
				if(allOrders[orderIndex].getCinemaShow().equals(ticket.getCinemaShow())){
					allOrders[orderIndex].addOrderLine(ticket, Quantity.of(1));
					allOrders[orderIndex].addTickets(ticket.getPrice());
				}
			}

			//5-15 Snacks hinzufügen
                        out:
			for(int i = 0; i < random.nextInt(5,16); i++){
                            if(allSnacks.isEmpty()){
                                System.getLogger(OrderDataInitializer.class.getName()).log(System.Logger.Level.WARNING, "Found no Snacks!");
                                break out;
                            }
				Snacks snack = allSnacks.get(random.nextInt(allSnacks.size()));
				allOrders[orderIndex].addOrderLine(snack, Quantity.of(1));
				allOrders[orderIndex].addSnacks(snack.getPrice());
			}
		}



		//Order abschließen
		for( int orderIndex = 0; orderIndex < generatedOrderCount; orderIndex++){
			allOrders[orderIndex].setPaymentMethod(Cash.CASH);
			orderManagement.payOrder(allOrders[orderIndex]);
			orderManagement.completeOrder(allOrders[orderIndex]);
		}

	}

}
