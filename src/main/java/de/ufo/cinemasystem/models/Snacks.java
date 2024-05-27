package de.ufo.cinemasystem.models;

import java.util.UUID;

import javax.money.MonetaryAmount;

import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;
import org.springframework.lang.NonNull;

import jakarta.persistence.EmbeddedId;
import lombok.Getter;

public class Snacks extends Product {

    public static enum SnackType {
        Getränk,
        Essen
    }

    private @EmbeddedId ProductIdentifier id = ProductIdentifier.of(UUID.randomUUID().toString());
    private @NonNull @Getter String name;
    private @NonNull @Getter MonetaryAmount price;
    private Metric metric;
    private SnackType type;
    private int count;

    public Snacks(String name, MonetaryAmount price) {
        this.name = name;
        this.price = price;
        this.count = 0;
        this.metric = Metric.UNIT;
    }

    public Snacks(String name, MonetaryAmount price, int count) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.count = count;
    }

    public ProductIdentifier getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MonetaryAmount getPrice() {
        return price;
    }

    public int addStock(int Count) {
        count += count;
        return count;
        // ToDO Logging einrichten
    }

    public int removeStock(int Count) {
        if (count >= Count) {
            count -= Count;
            return count;
        } else {
            return count;
        }
        // ToDO Logging einrichten
    }

}
