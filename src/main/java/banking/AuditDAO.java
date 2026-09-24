package banking;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import banking.BankingExceptions.NoTransactionsFoundException;

public interface AuditDAO {
    public void logTransaction(Account account, String targetID, TransactionType type, BigDecimal amount, Connection conn) throws SQLException;

    public List<Transaction> retrieveTransactions(Account account, Connection conn) throws SQLException, NoTransactionsFoundException;
}
