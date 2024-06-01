package de.ufo.cinemasystem.datainitializer;

import java.util.Random;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.models.Snacks.SnackType;
import de.ufo.cinemasystem.repository.SnacksRepository;

@Component
// Testdaten der Snacks werden nach Filmen und Kinosälen
// erstellt (deshalb: Order = 5)
@Order(5)
public class SnacksDataInitializer implements DataInitializer {

    private SnacksRepository snacksrepository;
    private static final Logger LOG = LoggerFactory.getLogger(SnacksDataInitializer.class);

    SnacksDataInitializer(SnacksRepository snacksRepository) {
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
                    Money.of(random.nextDouble(3.5, 20), "EUR"),
                    random.nextInt(10, 50),
                    type));
        }
    }

}