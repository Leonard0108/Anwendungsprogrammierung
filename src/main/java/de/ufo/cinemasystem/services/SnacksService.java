package de.ufo.cinemasystem.services;

import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.repository.SnacksRepository;
import jakarta.persistence.EntityNotFoundException;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class SnacksService {
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final SnacksRepository snacksRepository;

	public SnacksService(UniqueInventory<UniqueInventoryItem> inventory, SnacksRepository snacksRepository) {
		Assert.notNull(inventory, "Inventory darf nicht null sein!");
		Assert.notNull(inventory, "SnacksRepository darf nicht null sein!");

		this.inventory = inventory;
		this.snacksRepository = snacksRepository;
	}

	public Snacks createSnack(String name, Money money, Snacks.SnackType snackType, int stock) {
		if(stock < 0)
			throw new IllegalArgumentException("stock muss größer gleich null sein!");

		Snacks snack = new Snacks(name, money, snackType);
		this.snacksRepository.save(snack);
		this.inventory.save(new UniqueInventoryItem(snack, Quantity.of(stock)));
		return snack;
	}

	public void addStock(String id, int stock) {
		addStock(Product.ProductIdentifier.of(id), stock);
	}

	public void addStock(Product.ProductIdentifier id, int stock) {
		if(stock <= 0)
			throw new IllegalArgumentException("stock muss größer null sein!");

		Snacks snack = this.snacksRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);

		this.inventory.findByProduct(snack)
			.ifPresentOrElse(
				item -> {
					item.increaseQuantity(Quantity.of(stock));
					inventory.save(item);
				},
				() -> inventory.save(new UniqueInventoryItem(snack, Quantity.of(stock)))
			);
	}

	public void removeStock(String id, int stock) {
		removeStock(Product.ProductIdentifier.of(id), stock);
	}

	public void removeStock(Product.ProductIdentifier id, int stock) {
		if(stock <= 0)
			throw new IllegalArgumentException("stock muss größer null sein!");

		Snacks snack = this.snacksRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);

		this.inventory.findByProduct(snack)
			.ifPresentOrElse(
				item -> {
					item.decreaseQuantity(Quantity.of(stock));
					inventory.save(item);
				},
				() -> Assert.isTrue(true, String.format("Insufficient quantity! Have %s but was requested to reduce by %s.", 0, stock)
				/*Error Copied from Salespoint*/)
			);
	}

	/*
	public void setStock(String id, int stock) {
		setStock(Product.ProductIdentifier.of(id), stock);
	}

	public void setStock(Product.ProductIdentifier id, int stock) {
		if(stock < 0)
			throw new IllegalArgumentException("stock muss größer gleich null sein!");

		Snacks snack = this.snacksRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);

		// TODO: Gitb es eine Setter Möglichkeit?
		this.inventory.save(new UniqueInventoryItem(snack, Quantity.of(stock)));
	}
	 */

	/**
	 *
	 * @param id
	 * @return Wenn Snack nicht vorhanden oder nicht im Lager, so wird 0 zurückgegeben, sonst die Anzahl im Lager
	 */
	public int getStock(Product.ProductIdentifier id) {
		var optSnack =  this.snacksRepository.findById(id);
		if(optSnack.isEmpty()) return 0;
		var optItem = this.inventory.findByProduct(optSnack.get());
        return optItem.map(item -> item.getQuantity().getAmount().intValue()).orElse(0);
    }

	public int getStock(String id) {
		return getStock(Product.ProductIdentifier.of(id));
	}
}
