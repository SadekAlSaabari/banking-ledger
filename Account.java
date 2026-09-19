import java.math.BigDecimal;
import java.time.Instant;

public class Account {
    private final String id;
    private final String ownerName;
    private BigDecimal balance;
    private final Instant createdAt;
    
    public Account(String id, String name) {
        balance = BigDecimal.ZERO;
        this.id = id;
        ownerName = name;
        createdAt = Instant.now();
    }
}