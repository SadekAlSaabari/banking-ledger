import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {
    private final String id;
    private final String sourceAccountId;
    private final String targetAccountId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Instant carriedOutAt;

    public Transaction(String id, String sourceAccountId, String targetAccountId, TransactionType type, BigDecimal amount) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.type = type;
        this.amount = amount;
        carriedOutAt = Instant.now();
    }
}
