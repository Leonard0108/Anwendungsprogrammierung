package de.ufo.cinemasystem.models;

import javax.money.MonetaryAmount;

import org.salespointframework.catalog.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "SNACKS")
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

}
