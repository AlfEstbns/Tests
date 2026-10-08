package suspicious.model;
import java.math.BigDecimal;
import java.time.Instant;
public class InOperations {
    private String id;
    private String accountId;
    private String type; // "DEBIT" o "CREDIT"
    private BigDecimal amount;
    private String channel;
    private String deviceId;
    private Instant timestamp; // Usamos Instant para ISO 8601

    public InOperations() {
    }
    public InOperations(String id, String accountId, String type, BigDecimal amount,
                            String channel, String deviceId, String timestamp) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.channel = channel;
        this.deviceId = deviceId;
        this.timestamp = Instant.parse(timestamp); // Parsea automáticamente ISO 8601
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) {
        this.timestamp = (timestamp != null) ? Instant.parse(timestamp) : null;
    }
}
