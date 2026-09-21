import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class jdbcAuditDAO implements AuditDAO {
    String loggingQuery = "INSERT INTO transactions_audit (source_id, target_id, transaction_type, amount, carried_out_at) VALUES (?, ?, ?, ?, ?, ?)";
    
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
            e.printStackTrace();
            conn.rollback();
            throw e;
        }
    }
}
