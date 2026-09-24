package banking;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import banking.BankingExceptions.AccountNotFoundException;


public class jdbcAccountDAO implements AccountDAO {
    private final String createQuery = "INSERT INTO accounts (user_id, user_name, balance, created_at) VALUES (?, ?, ?, ?)";
    private final String retrieveQuery = "SELECT * FROM accounts WHERE user_id = ? AND user_name = ?";
    private final String viewBalanceQuery = "SELECT balance FROM accounts WHERE user_id = ?";
    private final String updateQuery = "UPDATE accounts SET balance = ? WHERE user_id = ?";

    @Override
    public void createAccount(Account account, Connection conn) throws SQLException, AccountNotFoundException {
        try (PreparedStatement ps = conn.prepareStatement(createQuery)) {
            ps.setString(1, account.getID());
            ps.setString(2, account.getOwnerName());
            ps.setBigDecimal(3, account.getBalance());
            ps.setString(4, account.getCreatedAt().toString());

            int changedRows = ps.executeUpdate();

            if (changedRows == 0) {
                throw new AccountNotFoundException("Account with ID: " + account.getID() + " was already created.");
            }
        }

        catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public Account retrieveAccount(Account account, Connection conn) throws AccountNotFoundException, SQLException {
        try (PreparedStatement ps = conn.prepareStatement(retrieveQuery)) {
            ps.setString(1, account.getID());
            ps.setString(2, account.getOwnerName());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account newAccount = new Account(
                    rs.getString("USER_ID"),
                    rs.getString("USER_NAME"),
                    rs.getBigDecimal("BALANCE")
                );
                return newAccount;
            } else {
                throw new AccountNotFoundException("Account with ID: " + account.getID() + " not found.");
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public String viewBalance(Account account, Connection conn) throws AccountNotFoundException, SQLException {
        try (PreparedStatement ps = conn.prepareStatement(viewBalanceQuery)) {
            ps.setString(1, account.getID());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("balance").toString();
            } else {
                throw new AccountNotFoundException("Account with ID: " + account.getID() + " not found.");
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void updateBalance(Account account, BigDecimal newBalance, Connection conn) throws AccountNotFoundException, SQLException {
        try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            ps.setBigDecimal(1, newBalance);
            ps.setString(2, account.getID());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new AccountNotFoundException("Account with ID: " + account.getID() + " not found.");
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
