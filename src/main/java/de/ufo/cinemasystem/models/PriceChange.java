package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;

/**
 * Interface ermöglicht das bearbeiten der Preise von Snacks und Filmen
 */
public interface PriceChange {

    /**
     * Get the ID of this object.
     * @return the id
     */
    String getIdString();

    /**
     * Get the name of this object.
     * @return name
     */
    String getName();

    /**
     * Get the price of this object.
     * @return price
     */
    Money getPrice();

    /**
     * set the price of this object.
     * @param newPrice new price
     */
    void setPrice(Money newPrice);

    /**
     * Check wether the post-constructor params have been set.
     * @return  true if they have.
     */
    boolean isInitialized();
}
