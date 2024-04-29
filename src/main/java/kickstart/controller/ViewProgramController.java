/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Jannik
 */
@Controller
public class ViewProgramController {
    
    /**
     * todo: where rights check?
     * @param week
     * @param m 
     */
    @GetMapping("/current-films/{week}")
    public void getCurrentProgram(@PathVariable String week , Model m){}
    
    
}
