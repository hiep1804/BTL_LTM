/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connection;

/**
 *
 * @author lehuy
 */
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sort_game";
    private static final String USER = "root";    
    private static final String PASS = "12345"; 
    
    public static Connection connect() throws ClassNotFoundException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            
            System.out.println("Connect the database successful");
        } catch (SQLException e) {
            System.out.println("Connect the database failed!");
            e.printStackTrace();
        }
        return connection;
    }
}