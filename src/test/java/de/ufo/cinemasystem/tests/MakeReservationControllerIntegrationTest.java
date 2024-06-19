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
class MakeReservationControllerIntegrationTest {
    
    @Autowired MockMvc mvc;
    
    /**
     * Checks that all controller methods are properly locked down.
     * @throws Exception 
     */
    @Test
    void testControllerLockdown() throws Exception{
        
        System.out.println("IntegrationTest: MakeReservationController");
        mvc.perform(get("/reserve-spots/reserve")) //
				.andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/reserve-spots/reserve")).andExpect(status().is3xxRedirection());
        
        mvc.perform(get("/reserve-spots/reserve/1")) //
				.andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/reserve-spots/add-ticket")).andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/reserve-spots/remove-ticket")).andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/reserve-spots/commit")).andExpect(status().is3xxRedirection());
    }
}
