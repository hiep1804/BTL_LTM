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
    
    /**
     * Cập nhật thống kê sau trận đấu
     * @param username Tên người chơi
     * @param won true nếu thắng, false nếu hòa hoặc thua
     * @param draw true nếu hòa
     * @return true nếu cập nhật thành công
     */
    public boolean updateMatchStats(String username, boolean won, boolean draw) {
        String sql = "UPDATE players SET matches_played = matches_played + 1, " +
                     "total_wins = total_wins + ?, " +
                     "total_score = total_score + ? " +
                     "WHERE username = ?";
        
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int winsToAdd = won ? 1 : 0;
            int scoreToAdd = won ? 10 : (draw ? 5 : 0);
            
            stmt.setInt(1, winsToAdd);
            stmt.setInt(2, scoreToAdd);
            stmt.setString(3, username);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
