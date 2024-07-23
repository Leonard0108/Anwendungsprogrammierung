
package de.ufo.cinemasystem.tests;

import de.ufo.cinemasystem.models.CinemaShow;
import de.ufo.cinemasystem.repository.CinemaShowRepository;
import java.util.List;
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
        System.out.println("Testing SpotsViewController");
        List<CinemaShow> toList = showsRepo.findAll().toList();
        System.out.println("(" + toList.size() + " shows)");
        
        for(CinemaShow c:toList){
            System.out.println("(" + c.getId() + ", " + c.getName() + ")");
            mvc.perform(get("/include/spots-view/" + c.getId())) //
                .andExpect(status().isOk());
        }
        
    }
}
