package de.ufo.cinemasystem.datainitializer;

import de.ufo.cinemasystem.models.FilmProvider;
import de.ufo.cinemasystem.repository.FilmProviderRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * DataInitialiser for film providers.
 * @author Yannick Harnisch
 */
@Component
@Order(1)
public class FilmProviderDataInitializer implements DataInitializer {

	private final FilmProviderRepository filmProviderRepository;
        private static final Logger LOG = LoggerFactory.getLogger(CinemaHallDataInitializer.class);

        /**
         * Erstelle einen neuen Initialiser mit den angegebenen Abhängigkeiten.
         * @param filmProviderRepository Implementierung Film-Provider-Repository.
         */
	FilmProviderDataInitializer(FilmProviderRepository filmProviderRepository) {
		Assert.notNull(filmProviderRepository, "FilmProviderRepository must not be null!");

		this.filmProviderRepository = filmProviderRepository;
	}

	@Override
	public void initialize() {

    if(filmProviderRepository.findAll().iterator().hasNext()){
        //leak unbenutzer einträge verhindern
        return;
    }
    LOG.info("Creating film providers...");
		for(int i = 1; i <= 3; i++) {
			this.filmProviderRepository.save(new FilmProvider("Film-Anbieter " + i));
		}

		System.out.println("Film-Anbieter:");
		this.filmProviderRepository.findAll().forEach(fp -> {
			System.out.println("ID = " + fp.getId());
			System.out.println("Name = " + fp.getName());
			System.out.println("=======================================");
		});
	}
}
