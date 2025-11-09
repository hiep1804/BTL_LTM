package shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Data transfer object for round information
 */
public class RoundInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int currentRound;
    private int totalRounds;
    private int timeSeconds;
    private ArrayList<Integer> array;

    public RoundInfo() {
    }

    public RoundInfo(int currentRound, int totalRounds, int timeSeconds, ArrayList<Integer> array) {
        this.currentRound = currentRound;
        this.totalRounds = totalRounds;
        this.timeSeconds = timeSeconds;
        this.array = array;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(int timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    public ArrayList<Integer> getArray() {
        return array;
    }

    public void setArray(ArrayList<Integer> array) {
        this.array = array;
    }
}
