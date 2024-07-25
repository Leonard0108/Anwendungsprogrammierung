
package de.ufo.cinemasystem.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.repository.CinemaShowRepository;

/**
 *
 * @author Jannik Schwaß
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SpotsViewControllerTests {
    
    @Autowired
    MockMvc mvc;
    
    @Autowired
    CinemaShowRepository showsRepo;
    
    /**
     * Checks seat view generation for all cinemaShows in the database (which are, rn, the ones created by the default initialiser)
     */
    @Test
    void testDefaultCinemaShows() throws Exception{
        StopWatch test = new StopWatch();
        System.out.println("Testing SpotsViewController");
        test.start("Repoabfrage");
        List<CinemaShow> toList = showsRepo.findAll().toList();
        test.stop();
        System.out.println("(" + toList.size() + " shows)");
        
        for(CinemaShow c:toList){
            mvc.perform(get("/include/spots-view/" + c.getId())) //
                .andExpect(status().isOk());
        }
        
    }
}
