package banking;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import banking.Myexceptions.AccountNotFoundException;

public interface AccountDAO {
    public void createAccount(Account account, Connection con) throws AccountNotFoundException, SQLException;

    public Account retrieveAccount(Account account, Connection con) throws AccountNotFoundException, SQLException;

    public String viewBalance(Account account, Connection con) throws AccountNotFoundException, SQLException;

    public void updateBalance(Account account, BigDecimal newBalance, Connection con) throws AccountNotFoundException, SQLException;
}