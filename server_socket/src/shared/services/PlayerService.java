package shared.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connection.DBConnection;
import shared.Player;

public class PlayerService {
    public ArrayList<Player> getLeaderBoard() {
        ArrayList<Player> leaderboard = new ArrayList<>();
        String sql = "SELECT username, name, total_score, total_wins, matches_played FROM players ORDER BY total_score DESC, total_wins DESC, matches_played DESC LIMIT 30";
        
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Player p = new Player();
                p.setUsername(rs.getString("username"));
                p.setName(rs.getString("name"));
                p.setTotalScore(rs.getInt("total_score"));
                p.setTotalWins(rs.getInt("total_wins"));
                p.setMatchesPlayed(rs.getInt("matches_played"));
                leaderboard.add(p);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }
    
    public Player getPlayer(String username) {
        String sql = "SELECT * FROM players WHERE username = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Player p = new Player();
                    p.setUsername(rs.getString("username"));
                    p.setName(rs.getString("name"));
                    p.setTotalScore(rs.getInt("total_score"));
                    p.setTotalWins(rs.getInt("total_wins"));
                    p.setMatchesPlayed(rs.getInt("matches_played"));
                    return p;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
