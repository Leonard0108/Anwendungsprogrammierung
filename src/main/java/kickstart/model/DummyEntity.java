/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Dummy Entity used to temporarely resolve linking problems while the other class is being written.
 * @author Jannik
 */
@Entity
@Table(name="DEBUGDUMMYS")
public class DummyEntity {
    public DummyEntity(){
        System.getLogger(DummyEntity.class.getName()).log(System.Logger.Level.WARNING, "Unresolved link");
    }
    
    private @Id @GeneratedValue int id;
}
