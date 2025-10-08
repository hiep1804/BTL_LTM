package shared;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;;
public class JDBC {
    public static Connection getconnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/ltm","root","mysql1612");
    }
}
