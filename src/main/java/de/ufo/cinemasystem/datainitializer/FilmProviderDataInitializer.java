package de.ufo.cinemasystem.datainitializer;

import de.ufo.cinemasystem.models.FilmProvider;
import de.ufo.cinemasystem.repository.FilmProviderRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import org.salespointframework.core.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(1)
public class FilmProviderDataInitializer implements DataInitializer {

	private final FilmProviderRepository filmProviderRepository;

	FilmProviderDataInitializer(FilmProviderRepository filmProviderRepository) {
		Assert.notNull(filmProviderRepository, "FilmProviderRepository must not be null!");

		this.filmProviderRepository = filmProviderRepository;
	}

	@Override
	public void initialize() {
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
