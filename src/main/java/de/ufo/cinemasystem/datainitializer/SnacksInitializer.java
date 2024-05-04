package de.ufo.cinemasystem.datainitializer;

import org.salespointframework.core.DataInitializer;

import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.models.Snacks.SnackType;
import de.ufo.cinemasystem.repository.SnacksRepository;

import java.util.Random;

import org.javamoney.moneta.Money;

public class SnacksInitializer implements DataInitializer {

    private final SnacksRepository snacksrepository;

    SnacksInitializer(SnacksRepository snacksRepository) {
        this.snacksrepository = snacksRepository;
    }

    @Override
    public void initialize() {
        if (snacksrepository.findAll().iterator().hasNext()) {
            return;
        }
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            SnackType type = (i % 2 == 0) ? SnackType.Essen : SnackType.Getränk;
            snacksrepository.save(new Snacks(
                    "Snack " + i,
                    type,
                    Money.of(random.nextDouble(3.5, 20), "EUR"),
                    random.nextInt(10, 50)));
        }
    }

}