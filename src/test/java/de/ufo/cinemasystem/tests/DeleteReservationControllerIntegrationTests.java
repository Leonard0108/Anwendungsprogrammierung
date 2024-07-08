package de.ufo.cinemasystem.tests;

import de.ufo.cinemasystem.models.Reservation;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import de.ufo.cinemasystem.repository.UserRepository;
import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 *
 * @author Jannik Schwaß
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
class DeleteReservationControllerIntegrationTests {

    @Autowired
    MockMvc mvc;

    private @Autowired
    CinemaShowRepository cinemaShowRepository;

    private @Autowired
    UserRepository uRepo;

    @Test
    void testControllerLockdown() throws Exception {
        System.out.println("IntegrationTest: DeleteReservationController");
        mvc.perform(get("/my-reservations")) //
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/my-reservations/delete/")).andExpect(status().is3xxRedirection());

        mvc.perform(get("/cancel-reservation/1"))
                .andExpect(status().is3xxRedirection());

        mvc.perform(post("/cancel-reservation/1")).andExpect(status().is3xxRedirection());

        mvc.perform(get("/reservations/find"))
                .andExpect(status().is3xxRedirection());

        mvc.perform(post("/reservations/find")).andExpect(status().is3xxRedirection());

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testDeleteReservation() throws Exception {
        mvc.perform(get("/my-reservations"))
                .andExpect(status().isOk());
        //create a dummy reservation
        Reservation work = new Reservation(uRepo.findByUserAccountUsername("user"), cinemaShowRepository.findById(5L).orElseThrow());

        boolean ok = false;

        out:
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) {
                if (!work.getCinemaShow().containsSeat(i, j)) {
                    continue;
                }
                if (!(work.getCinemaShow().getOccupancy(i, j).orElseThrow(() -> new InternalError("seat vanished in a black hole.")) == Seat.SeatOccupancy.FREE)) {
                    continue;
                }
                ok = true;
                mvc.perform(post("/reserve-spots/add-ticket")
                        .param("ticketType", "adult")
                        .param("spot", MakeReservationControllerIntegrationTest.toSpot(i, j))
                        .sessionAttr("current-reservation", work)
                        .sessionAttr("current-reservation-privileged", false)
                ).andExpect(status().isOk());
                System.out.println(java.util.Arrays.toString(work.getTickets()));
                break out;
            }
        }

        if (!ok) {
            throw new InternalError("failed to find a spot");
        }
        mvc.perform(post("/reserve-spots/commit")
                .sessionAttr("current-reservation", work)
        ).andExpect(status().is3xxRedirection());
        //check the list once more
        mvc.perform(get("/my-reservations"))
                .andExpect(status().isOk());
        //now check the 2 delete form endpoints
        mvc.perform(get("/cancel-reservation/1"))
                .andExpect(status().isOk());
        mvc.perform(post("/my-reservations/delete/")
                .param("reserveNumber", "1")
        )
                .andExpect(status().isOk());
        //now check delete 
        mvc.perform(post("/cancel-reservation/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "boss", roles = "BOSS")
    void testFindReservation() throws Exception {
        mvc.perform(get("/reservations/find"))
                .andExpect(status().isOk());

        mvc.perform(post("/reservations/find").param("query", "jfdigfdij"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Kein Benutzer gefunden.")));
        mvc.perform(post("/reservations/find").param("query", "boss"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Keine Reservierungen gefunden")));
    }
}
