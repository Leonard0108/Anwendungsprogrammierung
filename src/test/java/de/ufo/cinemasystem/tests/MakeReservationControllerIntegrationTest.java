package de.ufo.cinemasystem.tests;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.models.Seat;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import java.util.List;
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
class MakeReservationControllerIntegrationTest {
    
    @Autowired MockMvc mvc;
    
    @Autowired
    CinemaShowRepository showsRepo;
    
    /**
     * Checks that all controller methods are properly locked down.
     * @throws Exception 
     */
    @Test
    void testControllerLockdown() throws Exception{
        
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
    
    @Test
    @WithMockUser(username = "user" , roles = "USER")
    void testReservationFlow() throws Exception{
        CinemaShow toTest = null;
        List<CinemaShow> toList = showsRepo.findAll().toList();
        for(CinemaShow c:toList){
            if(c.canReserveSpots()){
                toTest = c;
                break;
            }
        }
        if(toTest == null){
            throw new InternalError("Couldn't find a cinemaShow to reserve spots in!");
        }
        
        mvc.perform(get("/reserve-spots/reserve/" + toTest.getId())) //
				.andExpect(status().isOk());
        
        boolean ok = false;
        
        /*out:
        for(int i = 0; i < 18;i++){
        for(int j = 0; j < 18; j++){
        if(!toTest.containsSeat(i, j)){
        continue;
        }
        if(!(toTest.getOccupancy(i, j).orElseThrow(() -> new InternalError("seat vanished in a black hole.")) == Seat.SeatOccupancy.FREE)){
        continue;
        }
        ok=true;
        mvc.perform(post("/reserve-spots/add-ticket")
        .param("ticketType", "adult")
        .param("spot", toSpot(i,j))
        ).andExpect(status().isOk());
        break out;
        }
        }
        
        if(!ok){
        throw new InternalError("couldn't find a spot");
        }*/
        
        
        mvc.perform(post("/reserve-spots/commit")).andExpect(status().is3xxRedirection());
    }

    private String toSpot(int row, int j) {
        return ((char) ('A' + row)) + ("" + j);
    }
}
