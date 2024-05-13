package de.ufo.cinemasystem.datainitializer;

import java.util.Random;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.ufo.cinemasystem.models.Snacks.SnackType;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.services.SnacksService;

@Component
@Order(10)
public class SnacksDataInitializer implements DataInitializer {
    private final SnacksService snacksService;
	private final SnacksRepository snacksRepository;

	public SnacksDataInitializer(SnacksService snacksService, SnacksRepository snacksRepository) {
		Assert.notNull(snacksService, "snacksService darf nicht null sein!");
		Assert.notNull(snacksRepository, "snacksRepository darf nicht null sein!");

		this.snacksService = snacksService;
		this.snacksRepository = snacksRepository;
	}

    @Override
    public void initialize() {
        if (snacksRepository.findAll().iterator().hasNext()) {
            return;
        }
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            SnackType type = (i % 2 == 0) ? SnackType.Essen : SnackType.Getränk;

			snacksService.createSnack(
				"Snack " + i,
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				type,
				random.nextInt(10, 50)
			);
        }
    }

}