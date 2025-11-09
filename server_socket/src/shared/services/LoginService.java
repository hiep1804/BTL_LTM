/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.services;

/**
 *
 * @author lehuy
 */
import shared.Player;
import connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {
    public boolean login(Player p) {
        if (p == null || p.getUsername() == null || p.getPassword() == null)    return false;
        String u = p.getUsername().trim();
        String password = p.getPassword().trim();
        if(u.isEmpty() || password.isEmpty()) return false;
        
         try(Connection conn = DBConnection.connect()) {
            if(conn == null)        return false;
            
            // Lấy thông tin đầy đủ của người chơi
            String sql = "SELECT username, name, total_score, total_wins, matches_played FROM players WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, u);
                stmt.setString(2, password);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Cập nhật thông tin vào object Player
                        p.setUsername(rs.getString("username"));
                        p.setName(rs.getString("name"));
                        p.setTotalScore(rs.getInt("total_score"));
                        p.setTotalWins(rs.getInt("total_wins"));
                        p.setMatchesPlayed(rs.getInt("matches_played"));
                        return true;
                    }
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }
}