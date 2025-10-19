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

public class RegisterService {
    public boolean register(Player p) {
        if (p == null || p.getUsername() == null || p.getPassword() == null)        return false;
        String u = p.getUsername().trim();
        String pw = p.getPassword().trim();
        
        try(Connection conn = DBConnection.connect()) {
            if(conn == null)     return false;
            
            // Kiểm tra username đã tồn tại chưa
            String checkSql = "SELECT COUNT(*) FROM players WHERE username = ?";
            try(PreparedStatement  checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, u);
                ResultSet rs = checkStmt.executeQuery();
                if(rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
            
            //Thêm mới 1 Player 
            String insertSql = "INSERT INTO players (username, password, name, total_score, total_wins, matches_played) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, p.getUsername().trim());
                insertStmt.setString(2, p.getPassword().trim());
                insertStmt.setString(3, p.getName().trim());
                insertStmt.setInt(4, 0);
                insertStmt.setInt(5, 0);
                insertStmt.setInt(6, 0);
                int rows = insertStmt.executeUpdate();
                return rows > 0;
            }
        } catch(Exception e) {
            return false;
        }
    }
}
