package banking;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import banking.Myexceptions.NoTransactionsFoundException;

public class jdbcAuditDAO implements AuditDAO {
    private final String loggingQuery = "INSERT INTO transactions_audit (source_id, target_id, transaction_type, amount, carried_out_at) VALUES (?, ?, ?, ?, ?)";
    private final String retrievalQuery = "SELECT * FROM transactions_audit WHERE source_id = ? OR target_id = ? ORDER BY carried_out_at DESC";

    
    public jdbcAuditDAO() {
    }
    
    @Override
    public void logTransaction(Account account, String targetID, TransactionType type, BigDecimal amount, Connection conn) throws SQLException {
        try (
            PreparedStatement ps = conn.prepareStatement(loggingQuery)
        ){
            ps.setString(1, account.getID());
            if (targetID != null) {
                ps.setString(2, targetID);
            } else {
                ps.setString(2, null);
            }
            ps.setString(3, type.getType());
            ps.setBigDecimal(4, amount);
            ps.setTimestamp(5, java.sql.Timestamp.from(Instant.now()));

            ps.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public ResultSet retrieveTransactions(Account account, Connection conn) throws SQLException, NoTransactionsFoundException {
        try (PreparedStatement ps = conn.prepareStatement(retrievalQuery)) {
            ps.setString(1, account.getID());
            ps.setString(2, account.getID());
            ResultSet results = ps.executeQuery();
            if (!results.next()) {
                throw new NoTransactionsFoundException("No transactions found for account with ID: " + account.getID());
            } else {
                return results;
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
