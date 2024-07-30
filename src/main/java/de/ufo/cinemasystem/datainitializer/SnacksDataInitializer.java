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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * DataInitialiser for Snacks.
 * @author Yannick Harnisch
 * @author Simon Liepe
 */
@Component
// Testdaten der Snacks werden nach Filmen und Kinosälen
// erstellt (deshalb: Order = 5)
@Order(5)
public class SnacksDataInitializer implements DataInitializer {

    private SnacksRepository snacksrepository;
    private SnacksService snacksService;
    private static final Logger LOG = LoggerFactory.getLogger(SnacksDataInitializer.class);

    /**
     * Create a new initaliser using the specified dependencies.
     * @param snacksRepository Implementation of SnacksRepository
     * @param snacksService Snack Service.
     */
    SnacksDataInitializer(SnacksRepository snacksRepository, SnacksService snacksService) {
        this.snacksrepository = snacksRepository;
        this.snacksService = snacksService;
    }

    @Override
    public void initialize() {
        if (snacksrepository.findAll().iterator().hasNext()) {
            return;
        }
        LOG.info("Creating default snacks...");
        Random random = new Random();

		try {
			Snacks s = new Snacks(
				"Cola",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Getränk,
				loadImageAsByteArray("cola.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Fanta",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Getränk,
				loadImageAsByteArray("fanta.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Eistee",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Getränk,
				loadImageAsByteArray("eistee.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Slush",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Getränk,
				loadImageAsByteArray("slush-getrank.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Eis",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("eis.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Gummibärchen",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("gummibarchen.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Nachos",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("nachos.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Popcorn (klein)",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("popcorn-smal.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Popcorn (mittel)",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("popcorn-mid.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Popcorn (groß)",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("popcorn-big.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

		try {
			Snacks s = new Snacks(
				"Schokoladenriegel",
				Money.of(random.nextDouble(3.5, 20), "EUR"),
				SnackType.Essen,
				loadImageAsByteArray("schokoladenriegel.png"));
			snacksrepository.save(s);
			snacksService.addStock(s.getId(), 50);
		}catch (IOException ex) {
                    LOG.error("I/O issiue setting up default snacks", ex);
                }

        snacksrepository.findAll().forEach(f -> {
			System.out.println(f.toString());
			System.out.println("Name: " + f.getName());
			System.out.println("Preis: " + f.getPrice());
			System.out.println("=======================================");
		});
        System.out.println("Unterstützte Dateiformate: ");
        String[] readerMIMETypes = javax.imageio.ImageIO.getReaderMIMETypes();
        for(String type:readerMIMETypes){
            System.out.println(type);
        }
    }

    /**
     * get an image as a byte array
     * @param filename internal filename under {@code static/assets/snacks/}
     * @return byte array
     * @throws IOException if the file can't be read.
     */
	public byte[] loadImageAsByteArray(String filename) throws IOException {
		ClassPathResource imgFile = new ClassPathResource("static/assets/snacks/" + filename);
		//inspired from https://stackoverflow.com/questions/1264709/convert-inputstream-to-byte-array-in-java
                ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream inputStream = imgFile.getInputStream()) {
            inputStream.transferTo(out);
        }
                return out.toByteArray();
	}

}