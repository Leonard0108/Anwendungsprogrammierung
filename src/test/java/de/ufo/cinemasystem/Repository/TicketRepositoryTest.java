package de.ufo.cinemasystem.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.FilmProvider;
import de.ufo.cinemasystem.models.Ticket;
import de.ufo.cinemasystem.models.Ticket.TicketCategory;
import de.ufo.cinemasystem.repository.TicketRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmProviderRepository;
import de.ufo.cinemasystem.repository.FilmRepository;

@SpringBootTest
public class TicketRepositoryTest {
    
    @Autowired
    private TicketRepository ticketRepo;
    @Autowired
    private FilmProviderRepository providerRepo;
    @Autowired
    private CinemaShowRepository csRepo;
    @Autowired
    private FilmRepository filmRepo;



    @Test
    public void TicketRepositoryTest_TicketSaveAndDelete_ReturnByIdIsNotNullOfEqualIdAndDeleted(){

        //Testdaten erstellen und Speichern im Repository
        FilmProvider provider = new FilmProvider("Testprovider");
        providerRepo.save(provider);
        Film film = new Film("Filmtest", "Test film für Testfälle", 90, 12, provider);
        film.setPrice(Money.of(10, "EUR"));
        filmRepo.save(film);
        CinemaShow cs = new CinemaShow(LocalDateTime.now().plusDays(1), Money.of(10, "EUR"), film);
        csRepo.save(cs);
        Ticket ticket = new Ticket(TicketCategory.children, cs);
        ticketRepo.save(ticket);
        //Abrufen der Daten im Repository
        Ticket ticketToTest = ticketRepo.findById(ticket.getId()).get();
        CinemaShow csToTest = csRepo.findById(cs.getId()).get();
        Film filmToTest = filmRepo.findById(film.getId()).get();
        FilmProvider providerToTest = providerRepo.findById(provider.getId()).get();
        //Abgleich des abgefragten Tickets
        Assertions.assertNotNull(ticketToTest);
        Assertions.assertEquals(ticket, ticketToTest);
        //Abgleich der abgefragten CinemaShow
        Assertions.assertNotNull(csToTest);
        Assertions.assertEquals(cs.getId(), csToTest.getId());
        //Abgleich des abgefragten Films
        Assertions.assertNotNull(filmToTest);
        Assertions.assertEquals(film.getId(), filmToTest.getId());
        //Abgleich des abgefragten Provider
        Assertions.assertNotNull(providerToTest);
        Assertions.assertEquals(provider.getId(), providerToTest.getId());
        
        //Test auf das Löschen der Eingegebenen Daten
        //Löschen in den Repositories
        ticketRepo.delete(ticketToTest);
        csRepo.delete(csToTest);
        filmRepo.delete(filmToTest);
        providerRepo.delete(providerToTest);

        //Abfragen ob die Daten gelöscht sind
        Optional<Ticket> ticket2ToTest = ticketRepo.findById(ticketToTest.getId());
        Optional<CinemaShow> cs2ToTest = csRepo.findById(csToTest.getId());
        Optional<Film> film2ToTest = filmRepo.findById(filmToTest.getId());
        Optional<FilmProvider> provider2ToTest = providerRepo.findById(providerToTest.getId());
        //Überprüfen, ob die Testinstanzen leer sind
        Assertions.assertTrue(ticket2ToTest.isEmpty(),"Ticket gelöscht!");
        Assertions.assertTrue(cs2ToTest.isEmpty(), "Cinemashow gelöscht!");
        Assertions.assertTrue(film2ToTest.isEmpty(), "FIlm gelöscht!");
        Assertions.assertTrue(provider2ToTest.isEmpty(), "Provider gelöscht!");
        
    }

}
