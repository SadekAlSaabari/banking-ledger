package banking;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import banking.Myexceptions.NoTransactionsFoundException;

public interface AuditDAO {
    public void logTransaction(Account account, String targetID, TransactionType type, BigDecimal amount, Connection conn) throws SQLException;

    public ResultSet retrieveTransactions(Account account, Connection conn) throws SQLException, NoTransactionsFoundException;
}
