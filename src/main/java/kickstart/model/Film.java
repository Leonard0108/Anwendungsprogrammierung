/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kickstart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 *
 * @author Jannik
 */
@Entity
@Table(name= "FILMS")
public class Film {
    
    private @Id @GeneratedValue Long id;
    private @NotNull String title;
    private @NotNull String desc;
    //in minutes
    private int timePlaying;

    /**
     * Creates a new film object, with the specified title, (short) description &amp; timePlaying.
     * @param title
     * @param desc
     * @param timePlaying 
     * @throws NullPointerException if title or desc are null
     * @throws IllegalArgumentException if timePlaying &lt;= 0
     */
    public Film(String title, String desc, int timePlaying) {
        this.title = title;
        this.desc = desc;
        this.timePlaying = timePlaying;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public int getTimePlaying() {
        return timePlaying;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * Checks wether {@code this} and the passed object are identical. 
     * Two films are considered identical when they have the same id.
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Film other = (Film) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * Returns a string representation of this object
     * @return a string
     */
    @Override
    public String toString() {
        return "Film{" + "title=" + title + ", timePlaying=" + timePlaying + '}';
    }
    
    
}
