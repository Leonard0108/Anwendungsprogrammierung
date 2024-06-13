package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.models.Film;
import org.salespointframework.order.Order.OrderIdentifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.Orders;

@Repository
public interface OrdersRepository extends CrudRepository<Orders, OrderIdentifier>{
	@Override
	Streamable<Orders> findAll();
}