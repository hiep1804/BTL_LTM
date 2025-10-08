package shared.services;

/**
 *
 * @author lehuy
 */
import shared.model.Player;
import shared.connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterService {
    public boolean register(Player p) {
        if (p == null || p.getUsername() == null || p.getPassword() == null) return false;
        String u = p.getUsername().trim();
        String pw = p.getPassword().trim();
        if(u.isEmpty() || pw.isEmpty()) return false;
        
        try(Connection conn = DBConnection.connect()) {
            if(conn == null)        return false;
            
            // Kiểm tra username đã tồn tại chưa
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            try(PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, u);
                ResultSet rs = checkStmt.executeQuery();
                if(rs.next() && rs.getInt(1) > 0) {
                    return false;   //username đã tồn tại
                }
            }
            
            // Kiểm tra username đã tồn tại chưa
            String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, u);
                insertStmt.setString(2, pw);
                int rows = insertStmt.executeUpdate();
                return rows > 0;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
