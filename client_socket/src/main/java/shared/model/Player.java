package shared.model;

import java.io.Serializable;

public class Player implements Serializable{
    private String username;
    private String password;
    private boolean busy;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.busy = false;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isBusy() { return busy; }
    public void setBusy(boolean busy) { this.busy = busy; }

}