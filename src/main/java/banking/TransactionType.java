package banking;

public enum TransactionType {
    DEPOSIT("deposit"),
    WITHDRAWAL("withdrawal"),
    TRANSFER("transfer");

    private final String type;

    // constructor
    TransactionType(String type) {
        this.type = type;
    }

    // returns lowercase string representation of transaction type
    public String getType() {
        return type;
    }
}
