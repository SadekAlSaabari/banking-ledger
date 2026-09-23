package banking;
import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {
    // attributes
    private final String id;
    private final String sourceAccountId;
    private final String targetAccountId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Instant carriedOutAt;

    // constructor
    public Transaction(String id, String sourceAccountId, String targetAccountId, TransactionType type, BigDecimal amount) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.type = type;
        this.amount = amount;
        carriedOutAt = Instant.now();
    }

    public Transaction(String id, String accountId, TransactionType type, BigDecimal amount) {
        this.id = id;
        this.sourceAccountId = accountId;
        this.targetAccountId = null;
        this.type = type;
        this.amount = amount;
        carriedOutAt = Instant.now();
    }

    // getter methods
    public String getId() {
        return id;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getCarriedOutAt() {
        return carriedOutAt;
    }

    // check the type of transaction
    public boolean isDeposit() {
        return type == TransactionType.DEPOSIT;
    }

    public boolean isWithdrawal() {
        return type == TransactionType.WITHDRAWAL;
    }

    public boolean isTransfer() {
        return type == TransactionType.TRANSFER;
    }
}
