package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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
