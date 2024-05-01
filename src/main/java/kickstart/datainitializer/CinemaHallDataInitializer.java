package kickstart.datainitializer;

import kickstart.models.CinemaHall;
import kickstart.repository.CinemaHallRepository;
import kickstart.repository.CinemaShowRepository;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;

@Component
@Order(2)
public class CinemaHallDataInitializer implements DataInitializer {

	private final CinemaHallRepository cinemaHallRepository;

	public CinemaHallDataInitializer(CinemaHallRepository cinemaHallRepository) {
		Assert.notNull(cinemaHallRepository, "cinemaHallRepository must not be null!");

		this.cinemaHallRepository = cinemaHallRepository;
	}

	@Override
	public void initialize() {
		// verhindert doppelte Kinosaal-Einträge,
		// falls Testdaten bereits in der Datenbank
		if(cinemaHallRepository.findAll().iterator().hasNext()) {
			return;
		}

		// Erstelle 6 Kinosäle:
		for(int i = 0; i < 6; i++) {
			cinemaHallRepository.save(new CinemaHall("Saal " + i));
		}

		System.out.println("Kinosäle:");
		cinemaHallRepository.findAll().forEach(ch -> {
			System.out.println(ch.getName());
		});
	}
}
