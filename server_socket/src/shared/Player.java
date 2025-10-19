package shared;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private boolean busy = false;

    // Tính năng thêm
    private String name;
    private int total_score;
    private int total_wins;
    private int matches_played;

    public Player(String username, String password) throws Exception {
        this.username = username;
        this.password = password;
    }
    
    public Player(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter for busy
    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for total_score
    public int getTotalScore() {
        return total_score;
    }

    public void setTotalScore(int total_score) {
        this.total_score = total_score;
    }

    // Getter and Setter for total_wins
    public int getTotalWins() {
        return total_wins;
    }

    public void setTotalWins(int total_wins) {
        this.total_wins = total_wins;
    }

    // Getter and Setter for matches_played
    public int getMatchesPlayed() {
        return matches_played;
    }

    public void setMatchesPlayed(int matches_played) {
        this.matches_played = matches_played;
    }
}