/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.model;

/**
 *
 * @author lehuy
 */
import java.io.Serializable;
public class Player implements Serializable{
    //private int id;
    private static final long serialVersionUID = 1L; 
    private String username;
    private String password;
    private boolean busy;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.busy = false;
    }
    public Player() {}
    
    //Code ngu
//    public int getId() {return id;}
//    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isBusy() { return busy; }
    public void setBusy(boolean busy) { this.busy = busy; }

}
