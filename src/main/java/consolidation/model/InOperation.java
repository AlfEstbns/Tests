package consolidation.model;


public class InOperation {
    private String id;
    private String accountId;
    private String type; // "DEBIT" or "CREDIT"
    private double amount;
    private String currency;
    private String timestamp;

    //Constructor
    public InOperation() {
    }

    public InOperation(String id, String accountId, String type, double amount, String currency, String timestamp) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
