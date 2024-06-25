
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
public class ErrorControllerIntegrationTest {
    
    @Autowired MockMvc mvc;
    
    @Test
    void testDirectInvocation() throws Exception{
        System.out.println("ErrorController: Direct Invocation");
        
        mvc.perform(get("/error")) 
				.andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(username = "user" , roles = "USER")
    void testSampleErrorCase() throws Exception{
        mvc.perform(get("/sell-tickets")) 
				.andExpect(status().isForbidden());
    }
}
