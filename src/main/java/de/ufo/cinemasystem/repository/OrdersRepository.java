package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import org.salespointframework.order.Order.OrderIdentifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Orders;

import java.time.LocalDateTime;

/**
 * CRUD-Repository for Orders
 * @author Simon Liepe
 */
@Repository
public interface OrdersRepository extends CrudRepository<Orders, OrderIdentifier>{
	@Query("SELECT o FROM Orders o WHERE  o.show = :show")
	Streamable<Orders> findOrdersByCinemaShow(CinemaShow show);
}
