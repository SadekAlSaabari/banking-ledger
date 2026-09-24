package banking;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import banking.BankingExceptions.NoTransactionsFoundException;

public class jdbcAuditDAO implements AuditDAO {
    private final String loggingQuery = "INSERT INTO transaction_audit (source_id, target_id, transaction_type, amount, carried_out_at) VALUES (?, ?, ?, ?, ?)";
    private final String retrievalQuery = "SELECT * FROM transaction_audit WHERE source_id = ? OR target_id = ? ORDER BY carried_out_at DESC";

    
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
            ps.setString(5, Instant.now().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public List<Transaction> retrieveTransactions(Account account, Connection conn) throws SQLException, NoTransactionsFoundException {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(retrievalQuery)) {
            ps.setString(1, account.getID());
            ps.setString(2, account.getID());
            try (ResultSet results = ps.executeQuery()) {
                while (results.next()) {
                    transactions.add(new Transaction(
                        results.getString("transaction_id"),
                        results.getString("source_id"),
                        results.getString("target_id"),
                        TransactionType.valueOf(
                            results.getString("transaction_type").toUpperCase()
                        ),
                        results.getBigDecimal("amount"),
                        Instant.parse(results.getString("carried_out_at"))
                    ));
                }
            }
            if (transactions.isEmpty()) {
                throw new NoTransactionsFoundException("No transactions found for account with ID: " + account.getID());
            }
            return transactions;
        } catch (SQLException e) {
            throw e;
        }
    }
}
