
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
class MiscPagesControllerIntegrationTests {
    
    @Autowired
    MockMvc mvc;
    
    @Test
    void testMiscPagesIntegration() throws Exception{
        System.out.println("IntegrationTest: MiscPagesController");
        mvc.perform(get("/robots.txt")) 
				.andExpect(status().isOk());
        
        mvc.perform(get("/imprint")) 
				.andExpect(status().isOk());
        
        mvc.perform(get("/privacy")) 
				.andExpect(status().isOk());
    }
}
