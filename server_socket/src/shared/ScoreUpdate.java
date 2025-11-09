package shared;

import java.io.Serializable;

/**
 * Serializable payload that keeps the current scoreboard and the result of the latest submission.
 */
public class ScoreUpdate implements Serializable {
    private static final long serialVersionUID = 1L;

    private String player1Username;
    private int player1Score;
    private String player2Username;
    private int player2Score;
    private String submitterUsername;
    private boolean correct;

    public ScoreUpdate() {
    }

    public ScoreUpdate(String player1Username, int player1Score,
                       String player2Username, int player2Score,
                       String submitterUsername, boolean correct) {
        this.player1Username = player1Username;
        this.player1Score = player1Score;
        this.player2Username = player2Username;
        this.player2Score = player2Score;
        this.submitterUsername = submitterUsername;
        this.correct = correct;
    }

    public String getPlayer1Username() {
        return player1Username;
    }

    public void setPlayer1Username(String player1Username) {
        this.player1Username = player1Username;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public String getPlayer2Username() {
        return player2Username;
    }

    public void setPlayer2Username(String player2Username) {
        this.player2Username = player2Username;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public String getSubmitterUsername() {
        return submitterUsername;
    }

    public void setSubmitterUsername(String submitterUsername) {
        this.submitterUsername = submitterUsername;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
