package banking;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionManager {

    private final String DB_URL = "jdbc:sqlite:database.db";
    private final String createAccountsTable = "CREATE TABLE IF NOT EXISTS accounts (" +
                        "user_id TEXT(6) PRIMARY KEY," +
                        "user_name TEXT NOT NULL," +
                        "balance NUMERIC(10,2) NOT NULL," +
                        "created_at TEXT NOT NULL" +
                        ");";

    private final String createAuditTable = "CREATE TABLE IF NOT EXISTS transaction_audit (" +
                        "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "source_id TEXT(6) REFERENCES accounts (user_id) NOT NULL," +
                        "target_id TEXT(6)," +
                        "transaction_type TEXT NOT NULL," +
                        "amount NUMERIC(10,2) NOT NULL," +
                        "carried_out_at TEXT NOT NULL" +
                        ");";

    private final String initialAccounts = "INSERT OR IGNORE INTO accounts(user_id, user_name, balance, created_at) VALUES " +
                        "('A5I9N8', 'james', 1100, '2026-09-23T14:26:58.995755900Z')," +
                        "('N0T2A9', 'andrew', 0, '2026-09-24T16:08:45.960215100Z')";

    public DBConnectionManager() {
        
    }

    public Connection establishConnection() throws SQLException {
        try {
            Connection con = DriverManager.getConnection(DB_URL);
            con.setAutoCommit(false);
            return con;
        }
        catch (SQLException e) {
            throw e;
        }
    }

    public void initialiseDatabase() throws SQLException {
        try (Connection conn = establishConnection();
            Statement stmt = conn.createStatement();) {
            if (conn != null) {
                stmt.execute(createAccountsTable);
                stmt.execute(createAuditTable);
                stmt.execute(initialAccounts);
                conn.commit();
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
