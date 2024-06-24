package de.ufo.cinemasystem.datainitializer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import de.ufo.cinemasystem.models.Snacks;
import de.ufo.cinemasystem.models.Snacks.SnackType;
import de.ufo.cinemasystem.repository.SnacksRepository;
import de.ufo.cinemasystem.services.SnacksService;

@Component
// Testdaten der Snacks werden nach Filmen und Kinosälen
// erstellt (deshalb: Order = 5)
@Order(5)
public class SnacksDataInitializer implements DataInitializer {

    private SnacksRepository snacksrepository;
    private SnacksService snacksService;
    private static final Logger LOG = LoggerFactory.getLogger(SnacksDataInitializer.class);

    SnacksDataInitializer(SnacksRepository snacksRepository, SnacksService snacksService) {
        this.snacksrepository = snacksRepository;
        this.snacksService = snacksService;
    }

    @Override
    public void initialize() {
        if (snacksrepository.findAll().iterator().hasNext()) {
            return;
        }
        Random random = new Random();

		try {
			Snacks s = new Snacks(
				"Cola",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Getränk,
				loadImageAsByteArray("cola.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Fanta",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Getränk,
				loadImageAsByteArray("fanta.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Eistee",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Getränk,
				loadImageAsByteArray("eistee.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Slush",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Getränk,
				loadImageAsByteArray("slush-getrank.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Eis",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("eis.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Gummibärchen",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("gummibarchen.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Nachos",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("nachos.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Popcorn (klein)",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("popcorn-smal.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Popcorn (mittel)",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("popcorn-mid.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Popcorn (groß)",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("popcorn-big.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

		try {
			Snacks s = new Snacks(
				"Schokoladenriegel",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("schokoladenriegel.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ignored) {}

        snacksrepository.findAll().forEach(f -> {
			System.out.println(f.toString());
			System.out.println("Name: " + f.getName());
			System.out.println("Preis: " + f.getPrice());
			System.out.println("=======================================");
		});
    }

	public byte[] loadImageAsByteArray(String filename) throws IOException {
		ClassPathResource imgFile = new ClassPathResource("static/assets/snacks/" + filename);
		return Files.readAllBytes(imgFile.getFile().toPath());
	}

}