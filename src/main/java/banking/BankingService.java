package banking;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import banking.BankingExceptions.AccountNotFoundException;
import banking.BankingExceptions.InsufficientFundsException;
import banking.BankingExceptions.NoTransactionsFoundException;

public class BankingService {    
    private final jdbcAccountDAO accountDAO;
    private final jdbcAuditDAO auditDAO;
    private final DBConnectionManager dbManager;
    
    public BankingService() {
        accountDAO = new jdbcAccountDAO();
        auditDAO = new jdbcAuditDAO();
        dbManager = new DBConnectionManager();
    }

    public String generateNewAccountID() {
        // generates six character account ID in format "AXAXAX" where A is a random uppercase letter and X is a random digit
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        Random rand = new Random();

        StringBuilder newID = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (i % 2 == 0) {
                newID.append(alphabet.charAt(rand.nextInt(alphabet.length())));
            } else {
                newID.append(digits.charAt(rand.nextInt(digits.length())));
            }
        }
        return newID.toString();
    }

    public String createAccount(String name, BigDecimal initialBalance) throws SQLException, AccountNotFoundException {
        String newAccountID = generateNewAccountID();
        Account account = new Account(newAccountID, name, initialBalance);
        try (Connection conn = dbManager.establishConnection();){
            try {
                accountDAO.createAccount(account, conn);
            }
            catch (SQLException | AccountNotFoundException e) {
                conn.rollback();
                throw e;
            }
            conn.commit();
            return newAccountID;
        }
        catch (SQLException e) {
            throw e;
        }
    }
    
    public Account retrieveAccount (String accountID, String name, Connection conn) throws SQLException, AccountNotFoundException {
        Account account = new Account(accountID, name);
        try {
            Account retrievedAccount = accountDAO.retrieveAccount(account, conn);
            conn.commit();
            return retrievedAccount;
        }
        catch (SQLException | AccountNotFoundException e) {
            conn.rollback();
            throw e;
        }
    }

    public Account retrieveAccount (String accountID, String name) throws SQLException, AccountNotFoundException {
        try (Connection conn = dbManager.establishConnection();){
            try {
                Account retrievedAccount = retrieveAccount(accountID, name, conn);
                conn.commit();
                return retrievedAccount;
            }
            catch (SQLException | AccountNotFoundException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException | AccountNotFoundException e) {
            throw e;
        }
    }

    public String viewBalance(String accountID, String name) throws SQLException, AccountNotFoundException {
        Account account = new Account(accountID, name);
        try (Connection conn = dbManager.establishConnection();){
            try {
                String balance = accountDAO.viewBalance(account, conn);
                conn.commit();
                return balance;
            }
            catch (SQLException | AccountNotFoundException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException | AccountNotFoundException e) {
            throw e;
        }
    }

    public String viewBalance(Account account) throws SQLException, AccountNotFoundException {
        try (Connection conn = dbManager.establishConnection();){
            try {
                String balance = accountDAO.viewBalance(account, conn);
                conn.commit();
                return balance;
            }
            catch (SQLException | AccountNotFoundException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException | AccountNotFoundException e) {
            throw e;
        }
    }

    public void withdraw(String accountID, String name, BigDecimal amount) throws SQLException, AccountNotFoundException, InsufficientFundsException {
        try (Connection conn = dbManager.establishConnection();){
            Account retrievedAccount = retrieveAccount(accountID, name, conn);
            BigDecimal existingBalance = retrievedAccount.getBalance();
            if (existingBalance.compareTo(amount) >= 0) {
                BigDecimal newBalance = existingBalance.subtract(amount);
                accountDAO.updateBalance(retrievedAccount, newBalance, conn);
                auditDAO.logTransaction(retrievedAccount, null, TransactionType.WITHDRAWAL, amount, conn);
                conn.commit();
            }
            else {
                conn.rollback();
                throw new InsufficientFundsException("Insufficient funds for withdrawal.");
            }
        }
        catch (SQLException | AccountNotFoundException e) {
            throw e;
        }
    }

    public void deposit(String accountID, String name, BigDecimal amount) throws SQLException, AccountNotFoundException {
        try (Connection conn = dbManager.establishConnection();){
            try {
                Account retrievedAccount = retrieveAccount(accountID, name, conn);
                BigDecimal existingBalance = retrievedAccount.getBalance();
                BigDecimal newBalance = existingBalance.add(amount);
                accountDAO.updateBalance(retrievedAccount, newBalance, conn);
                auditDAO.logTransaction(retrievedAccount, null, TransactionType.DEPOSIT, amount, conn);

                conn.commit();
            } catch (SQLException | AccountNotFoundException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollBackException) {
                    e.addSuppressed(rollBackException);
                }
                throw e;
            }
        }
        catch (SQLException e) {
            throw e;
        }
    }

    public void transfer(String sourceAccountID, String sourceName, String targetAccountID, BigDecimal amount) throws SQLException, AccountNotFoundException, InsufficientFundsException {
        try (Connection conn = dbManager.establishConnection();){
            Account sourceAccount = retrieveAccount(sourceAccountID, sourceName, conn);
            Account targetAccount = retrieveAccount(targetAccountID, null, conn);
            BigDecimal sourceBalance = sourceAccount.getBalance();
            if (sourceBalance.compareTo(amount) >= 0) {
                BigDecimal newSourceBalance = sourceBalance.subtract(amount);
                BigDecimal newTargetBalance = targetAccount.getBalance().add(amount);
                accountDAO.updateBalance(sourceAccount, newSourceBalance, conn);
                accountDAO.updateBalance(targetAccount, newTargetBalance, conn);
                auditDAO.logTransaction(sourceAccount, targetAccountID, TransactionType.TRANSFER, amount, conn);
                conn.commit();
            }
            else {
                conn.rollback();
                throw new InsufficientFundsException("Insufficient funds for transfer.");
            }
        }
        catch (SQLException | AccountNotFoundException e) {
            throw e;
        }
    }
    
    public String retrieveTransactionHistory(String accountID, String name) throws SQLException, AccountNotFoundException, NoTransactionsFoundException {
        try (Connection conn = dbManager.establishConnection();){
            Account retrievedAccount = retrieveAccount(accountID, name, conn);
            StringBuilder transactionHistory = new StringBuilder();
            try {
                List<Transaction> transactions = auditDAO.retrieveTransactions(retrievedAccount, conn);
                for (Transaction transaction : transactions) {
                    transactionHistory.append(String.format(
                        "Source ID: %s, Target ID: %s, Type: %s, Amount: %s, Timestamp: %s%n",
                        transaction.getSourceAccountId(),
                        transaction.getTargetAccountId(),
                        transaction.getType().getType(),
                        transaction.getAmount().toPlainString(),
                        transaction.getCarriedOutAt()
                    ));
                }
            }
            catch (SQLException | NoTransactionsFoundException e) {
                conn.rollback();
                throw e;
            }
            conn.commit();
            return transactionHistory.toString();
        }
        catch (SQLException | AccountNotFoundException e) {
            throw e;
        }
    }
}
