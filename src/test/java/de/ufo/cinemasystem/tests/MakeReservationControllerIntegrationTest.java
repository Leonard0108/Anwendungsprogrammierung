package de.ufo.cinemasystem.tests;

import de.ufo.cinemasystem.models.CinemaHall;
import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Reservation;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.YearWeekEntry;
import de.ufo.cinemasystem.repository.CinemaHallRepository;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.FilmRepository;
import de.ufo.cinemasystem.repository.ReservationRepository;
import de.ufo.cinemasystem.repository.UserRepository;
import de.ufo.cinemasystem.services.CinemaShowService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import org.javamoney.moneta.Money;
import static org.hamcrest.core.StringContains.containsString;
import org.junit.jupiter.api.Assertions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 *
 * @author Jannik Schwaß
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
class MakeReservationControllerIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    CinemaShowRepository showsRepo;

    private @Autowired
    UserRepository uRepo;
    
    private @Autowired ReservationRepository repo;
    
    private @Autowired CinemaShowService cinemaShowService;
    
    private @Autowired CinemaHallRepository cinemaHallRepository;

    private @Autowired FilmRepository filmRepository;
    
    private @Autowired CinemaShowRepository cinemaShowRepository;

    /**
     * Checks that all controller methods are properly locked down.
     *
     * @throws Exception
     */
    @Test
    void testControllerLockdown() throws Exception {

        System.out.println("IntegrationTest: MakeReservationController");
        mvc.perform(get("/reserve-spots/reserve"))
                .andExpect(status().is3xxRedirection());

        mvc.perform(post("/reserve-spots/reserve")).andExpect(status().is3xxRedirection());

        mvc.perform(get("/reserve-spots/reserve/1"))
                .andExpect(status().is3xxRedirection());

        mvc.perform(post("/reserve-spots/add-ticket")).andExpect(status().is3xxRedirection());

        mvc.perform(post("/reserve-spots/remove-ticket")).andExpect(status().is3xxRedirection());

        mvc.perform(post("/reserve-spots/commit")).andExpect(status().is3xxRedirection());
    }

    /**
     * Test a full reservation. This testcase was not described in the presentation. We're doing this here so we don't have to endlessly repeat ourselves
     * when (re-)creating the session objects.
     * @throws Exception 
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testReservationFlow() throws Exception {
        System.out.println("IntegrationTest: MakeReservationController: Flow-Test");
        CinemaShow toTest = null;
        List<CinemaShow> toList = showsRepo.findAll().toList();
        for (CinemaShow c : toList) {
            if (c.canReserveSpots()) {
                toTest = c;
                break;
            }
        }
        if (toTest == null) {
            throw new InternalError("Couldn't find a cinemaShow to reserve spots in!");
        }
        System.out.println("MakeReservationController: Testing on " + toTest.getId());

        MvcResult rv = mvc.perform(get("/reserve-spots/reserve/" + toTest.getId())) //
                .andExpect(status().isOk()).andReturn();
        HttpSession session = rv.getRequest().getSession();
        Reservation work = null;
        if (session != null) {
            work = (Reservation) session.getAttribute("current-reservation");
        }
        if (work == null) {
            //yawn
            System.out.println("MakeReservationControllerIntegrationTests: No Reservation");
            //Note: If "user" was an invalid user the previous request would've failed
            work = new Reservation(uRepo.findByUserAccountUsername("user"), toTest);
        }

        boolean ok = false;

        out:
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) {
                if (!toTest.containsSeat(i, j)) {
                    continue;
                }
                if (!(toTest.getOccupancy(i, j).orElseThrow(() -> new InternalError("seat vanished in a black hole.")) == Seat.SeatOccupancy.FREE)) {
                    continue;
                }
                ok = true;
                mvc.perform(post("/reserve-spots/add-ticket")
                        .param("ticketType", "adult")
                        .param("spot", toSpot(i, j))
                        .sessionAttr("current-reservation", work)
                        .sessionAttr("current-reservation-privileged", false)
                ).andExpect(status().isOk());
                System.out.println(java.util.Arrays.toString(work.getTickets()));
                break out;
            }
        }

        if (!ok) {
            throw new InternalError("couldn't find a spot");
        }

        mvc.perform(post("/reserve-spots/commit").sessionAttr("current-reservation", work)).andExpect(status().is3xxRedirection());
        Assertions.assertNotEquals(0,repo.count(),"commit failed");
    }
    
    /**
     * Test various invalid requests. This testcase wasn't described in the presentation.
     * @throws Exception 
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testInvalids() throws Exception{
        System.out.println("IntegrationTest: MakeReservationController: Invalids");
        System.out.println("IntegrationTest: MakeReservationController: non-entrance endpoint without session variables");
        mvc.perform(post("/reserve-spots/add-ticket")).andExpect(status().is3xxRedirection());

        mvc.perform(post("/reserve-spots/remove-ticket")).andExpect(status().is3xxRedirection());

        mvc.perform(post("/reserve-spots/commit")).andExpect(status().is3xxRedirection());
        
        
        System.out.println("IntegrationTest: MakeReservationController: Nonexistant cinemaShow");
        mvc.perform(get("/reserve-spots/reserve/57858832589375"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bitte wählen sie eine Veranstaltung aus der Liste!")));
        
        mvc.perform(post("/reserve-spots/reserve").param("event", "57858832589375"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bitte wählen sie eine Veranstaltung aus der Liste!")));
        
        System.out.println("IntegrationTest: MakeReservationController: No longer reserveable");
        mvc.perform(get("/reserve-spots/reserve/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Reservierungen sind nur bis 45 Minuten vor Vorstellungsbeginn möglich!")));
        
        mvc.perform(post("/reserve-spots/reserve").param("event", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Reservierungen sind nur bis 45 Minuten vor Vorstellungsbeginn möglich!")));
        System.out.println("IntegrationTest: MakeReservationController: Not yet reserveable");
        
        /*Film get = filmRepository.findAvailableNow().toList().get(0);
        if(!get.isAvailableAt(LocalDateTime.now().plusDays(14))){
        get.addRentWeek(YearWeekEntry.getNowYearWeek().nextWeek().nextWeek());
        filmRepository.save(get);
        }
        CinemaHall ch = cinemaHallRepository.findById(2L).orElseThrow();
        CinemaShow createdCinemaShow = cinemaShowService.createCinemaShow(LocalDateTime.now().plusDays(14), Money.of(20, "EUR"), get, ch);
        mvc.perform(get("/reserve-spots/reserve/" + createdCinemaShow.getId()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Für diese Veranstaltung können noch keine Plätze reserviert werden!")));
        
        mvc.perform(post("/reserve-spots/reserve").param("event", Long.toString(createdCinemaShow.getId())))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Für diese Veranstaltung können noch keine Plätze reserviert werden!")));*/
        
        Reservation work = new Reservation(uRepo.findByUserAccountUsername("user"), cinemaShowRepository.findById(5L).orElseThrow());
        System.out.println("IntegrationTest: MakeReservationController: AddTicket invalid params");
        //spot
        mvc.perform(post("/reserve-spots/add-ticket")
                        .param("ticketType", "adult")
                        .param("spot", "Z12")
                        .sessionAttr("current-reservation", work)
                        .sessionAttr("current-reservation-privileged", false)
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("Ungültiger Sitzplatz:")));
        //ticketType
        mvc.perform(post("/reserve-spots/add-ticket")
                        .param("ticketType", "rejigrejigjijg")
                        .param("spot", "B0")
                        .sessionAttr("current-reservation", work)
                        .sessionAttr("current-reservation-privileged", false)
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("Nicht existenter Kartentyp")));
        //reserved spot
        boolean ok = false;
        CinemaShow toTest = work.getCinemaShow();

        out:
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) {
                if (!toTest.containsSeat(i, j)) {
                    continue;
                }
                if (!(toTest.getOccupancy(i, j).orElseThrow(() -> new InternalError("seat vanished in a black hole.")) != Seat.SeatOccupancy.FREE)) {
                    continue;
                }
                ok = true;
                mvc.perform(post("/reserve-spots/add-ticket")
                        .param("ticketType", "adult")
                        .param("spot", toSpot(i, j))
                        .sessionAttr("current-reservation", work)
                        .sessionAttr("current-reservation-privileged", false)
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("Sitzplatz nicht mehr verfügbar")));
                break out;
            }
        }

        if (!ok) {
            throw new InternalError("couldn't find a spot");
        }
        System.out.println("IntegrationTest: MakeReservationController: RemoveTicket invalid params");
        
        mvc.perform(post("/reserve-spots/remove-ticket")
                .param("deleteCartEntry", "98er8gr093930848j4j389j934jggj8g8j93j834")
        ).andExpect(status().is3xxRedirection());
        System.out.println("IntegrationTest: MakeReservationController: commit 10 ticket limit for non-staff");
        
        ok = false;
        int count = 0;
        
        out:
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) {
                if (!toTest.containsSeat(i, j)) {
                    continue;
                }
                if (!(toTest.getOccupancy(i, j).orElseThrow(() -> new InternalError("seat vanished in a black hole.")) == Seat.SeatOccupancy.FREE)) {
                    continue;
                }
                if (count < 10) {
                    mvc.perform(post("/reserve-spots/add-ticket")
                            .param("ticketType", "adult")
                            .param("spot", toSpot(i, j))
                            .sessionAttr("current-reservation", work)
                            .sessionAttr("current-reservation-privileged", false)
                    ).andExpect(status().isOk());
                } else {
                    mvc.perform(post("/reserve-spots/add-ticket")
                            .param("ticketType", "adult")
                            .param("spot", toSpot(i, j))
                            .sessionAttr("current-reservation", work)
                            .sessionAttr("current-reservation-privileged", false)
                    ).andExpect(status().isOk())
                    .andExpect(content().string(containsString("Es können nur maximal 10 Tickets im voraus reserviert werden.")));
                    ok=true;
                    break out;
                }
                count +=1;
                System.out.println("MakeReservationController: (" + count + "/11)");
            }
        }
        
        if(!ok){
            throw new InternalError("Not enough free seats in cinema show to test ticket limit (C-ID: "+work.getCinemaShow().getId() + ", count: " + count + ")");
        }
    }

    static String toSpot(int row, int j) {
        return ((char) ('A' + row)) + ("" + j);
    }
}
