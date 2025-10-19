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
            
            //Đếm số lượng bản ghi phù hợp
            String sql = "SELECT COUNT(*) FROM players  WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, u);
                stmt.setString(2, password);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }
}