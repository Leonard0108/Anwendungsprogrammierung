package de.ufo.cinemasystem.models;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Snacks")
public class Snacks extends Product implements PriceChange {


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

    public Snacks(String name, MonetaryAmount price) {
        super(name, price);
    }

    public Snacks(String name, MonetaryAmount price, SnackType type) {
        super(name, price);
        this.type = type;
    }

	public String getSnackType() {
		return this.type.toString();
	}

	public String getIdString(){
		return "snack-" + super.getId().toString();
	}

	@Override
	public void setPrice(Money newPrice) {
		super.setPrice(newPrice);
	}

	@Override
	public Money getPrice(){
		return (Money) super.getPrice();
	}

	public boolean isInitialized(){
		return super.getPrice().getNumber().intValue() != -1;
	}

}
