package banking.BankingExceptions;

public class NoTransactionsFoundException extends Exception {
    public NoTransactionsFoundException(String message) {
        super(message);
    }
}
