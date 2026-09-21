import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {
    public DBConnectionManager() {
        
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");
            con.setAutoCommit(false);
            return con;
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
