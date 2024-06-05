package de.ufo.cinemasystem.models;

import org.javamoney.moneta.Money;

/**
 * Interface ermöglicht das bearbeiten der Preise von Snacks und Filmen
 */
public interface PriceChange {
	String getIdString();
	String getName();
	Money getPrice();
	void setPrice(Money newPrice);
	boolean isInitialized();
	}
