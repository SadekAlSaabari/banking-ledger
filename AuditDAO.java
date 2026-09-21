import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public interface AuditDAO {
    public void logTransaction(Account account, String targetID, TransactionType type, BigDecimal amount, Connection conn) throws SQLException;
}
