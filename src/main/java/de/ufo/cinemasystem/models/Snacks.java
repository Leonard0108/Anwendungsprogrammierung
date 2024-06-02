package de.ufo.cinemasystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.javamoney.moneta.Money;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.salespointframework.catalog.Product;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;

@Entity
@Table(name = "Snacks")
public class Snacks extends Product {

	public static enum SnackType {
		Getränk,
		Essen
	}

	// private @EmbeddedId ProductIdentifier id =
	// ProductIdentifier.of(UUID.randomUUID().toString());
	// private Metric metric;
	private SnackType type;

	@SuppressWarnings({ "unused", "deprecation" })
	private Snacks() {
	}

	public Snacks(String name, Money price) {
		super(name, price);
	}

	public Snacks(String name, Money price, SnackType type) {
		super(name, price);
		this.type = type;
	}

	public String getSnackType() {
		return this.type.toString();
	}
}
