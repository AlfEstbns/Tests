package suspicious.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutOperations {
    private String type; // "FREQUENT_DEBITS", "HIGH_AMOUNT", "SHARED_DEVICE"
    private String accountId;
    private String deviceId;
    private List<String> accounts;
    private List<String> relatedEventIds;

    public OutOperations() {}

    // Constructor Reglas 1 y 2 (basadas en cuenta)
    public OutOperations(String type, String accountId, List<String> relatedEventIds) {
        this.type = type;
        this.accountId = accountId;
        this.relatedEventIds = relatedEventIds;
    }

    // Constructor Regla 3 (basada en dispositivo)
    public OutOperations(String type, String deviceId, List<String> accounts, List<String> relatedEventIds) {
        this.type = type;
        this.deviceId = deviceId;
        this.accounts = accounts;
        this.relatedEventIds = relatedEventIds;
    }

    public String getType() { return type; }
    public String getAccountId() { return accountId; }
    public String getDeviceId() { return deviceId; }
    public List<String> getAccounts() { return accounts; }
    public List<String> getRelatedEventIds() { return relatedEventIds; }
}
