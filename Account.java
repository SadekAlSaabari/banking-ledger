import java.math.BigDecimal;
import java.time.Instant;

public class Account {
    // attributes
    private final String id;
    private final String ownerName;
    private final BigDecimal balance;
    private final Instant createdAt;
    
    // constructor
    public Account(String id, String name) {
        balance = BigDecimal.ZERO;
        this.id = id;
        ownerName = name;
        createdAt = Instant.now();
    }

    // getter methods
    public String getID() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}