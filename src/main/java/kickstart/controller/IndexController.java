/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.controller;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for "/"
 * @author Jannik
 */
public class IndexController {
    
    /**
     * TODO: actual index
     * @return 
     */
    @GetMapping("/")
    public String getIndexPage(){
        return "welcome";
    }
}
