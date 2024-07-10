package de.ufo.cinemasystem.models;

import javax.money.MonetaryAmount;

import de.ufo.cinemasystem.repository.CinemaHallRepository;
import jakarta.persistence.Lob;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a snack.
 * @author Simon Liepe
 * @author Tobias Knoll
 */
@Entity
@Table(name = "Snacks")
public class Snacks extends Product implements PriceChange {


    /**
     * Represents the snack type.
     * 
     */
	public static enum SnackType {
            /**
             * The snack is a drink.
             */
		Getränk,
                /**
                 * the snack is something to eat.
                 */
		Essen
	}

	// private @EmbeddedId ProductIdentifier id =
	// ProductIdentifier.of(UUID.randomUUID().toString());
	// private Metric metric;
	private SnackType type;

	// Binärdaten in der DB speichern
	@Lob
	private byte[] imageData;

	@SuppressWarnings({ "unused", "deprecation" })
	private Snacks() {
	}

        /**
         * Create a new snack.
         * @param name snack name
         * @param price snack price.
         */
    public Snacks(String name, MonetaryAmount price) {
        super(name, price);
    }

        /**
         * Create a new snack.
         * @param name snack name
         * @param price snack price.
     * @param type snack type.
         */
    public Snacks(String name, MonetaryAmount price, SnackType type) {
        super(name, price);
        this.type = type;
    }

    /**
         * Create a new snack.
         * @param name snack name
         * @param price snack price.
     * @param type snack type.
     * @param imageData an image of the snack.
         */
	public Snacks(String name, MonetaryAmount price, SnackType type, byte[] imageData) {
		super(name, price);
		this.type = type;
		this.imageData = imageData;
	}


        /**
         * Get the snack type.
         * @return the snack type.
         */
	public String getSnackType() {
		return this.type.toString();
	}

        /**
         * Get the snack id.
         * @return the snack id.
         */
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

        /**
         * Get a base64-encoded snack image.
         * @return the image, or {@code null} if there isn't one.
         */
	public String getImageBase64() {
		if (imageData != null) {
			return Base64.getEncoder().encodeToString(imageData);
		}
		return null;
	}
}
