package DAO;
import model.Player;
import shared.JDBC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.*;
public class PlayerDAO {
    public List<Player> getAllPlayers() throws SQLException{
        List<Player> players=new ArrayList<>();
        Connection conn=JDBC.getconnection();
        String sql="SELECT * FROM players ORDER BY total_score DESC, total_wins DESC";
        PreparedStatement ps=conn.prepareStatement(sql);
        ResultSet rs=ps.executeQuery();
        while(rs.next()){
            Player p=new Player(
                rs.getInt("player_id"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getInt("total_score"),
                rs.getInt("total_wins"),
                rs.getInt("matches_played")
            );
            players.add(p);
        }
        rs.close();
        ps.close();
        conn.close();
        return players;
    }
}
