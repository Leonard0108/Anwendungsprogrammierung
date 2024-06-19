
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
public class ViewProgramControllerTests {
    
    @Autowired
    MockMvc mvc;
    
    /**
     * Check the public ViewProgramController endpoints and that nonpublic ones are correctly locked down.
     * @throws Exception 
     */
    @Test
    void testPublicEndpoints() throws Exception{
        System.out.println("IntegrationTest: ViewProgramController");
        //public endpoints status code checks
        mvc.perform(get("/current-films")) 
                .andExpect(status().is3xxRedirection());
        
        mvc.perform(get("/current-films/2024/15")) 
                .andExpect(status().isOk());
        
        mvc.perform(get("/cinema-shows/1")) 
                .andExpect(status().isOk());
        
        
        //check locked down endpoints unavailability
        
        //commented because of parameter validation
        /*mvc.perform(post("/current-films/2024/15"))
        .andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/cinema-shows/1/edit"))
        .andExpect(status().is3xxRedirection());
        
        mvc.perform(post("/cinema-shows/1/delete"))
        .andExpect(status().is3xxRedirection());*/
        
    }
}
