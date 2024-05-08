package de.ufo.cinemasystem.models;

import jakarta.persistence.Entity;
import org.javamoney.moneta.Money;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Entity
public class Snacks {

	public static enum SnackType {
        Getränk,
        Essen
    }

    private @Id @GeneratedValue Long id;
    private String name;
    private SnackType type;
    private Money price;
    private int count;

	public Snacks() {}
    public Snacks(String name, SnackType type, Money price, int count) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

	public int getCount() { return count; }

    public int addStock(int count) {
        this.count += count;
        return this.count;
        // ToDO Logging einrichten
    }

    public int removeStock(int count) {
        if (this.count >= count) {
            this.count -= count;
        }
        return this.count;
        // ToDO Logging einrichten
    }

}
