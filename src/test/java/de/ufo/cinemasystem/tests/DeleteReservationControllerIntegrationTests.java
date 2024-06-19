package de.ufo.cinemasystem.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    void testControllerLockdown() throws Exception {
        System.out.println("IntegrationTest: DeleteReservationController");
        mvc.perform(get("/my-reservations")) //
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/my-reservations/delete/")).andExpect(status().is3xxRedirection());
        
        mvc.perform(get("/cancel-reservation/1")) //
                .andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/cancel-reservation/1")).andExpect(status().is3xxRedirection());
        
        mvc.perform(get("/reservations/find")) //
                .andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/reservations/find")).andExpect(status().is3xxRedirection());
    }
}
