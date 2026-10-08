package rules.model;

public class InOperationRules {
    private double amount;
    private String currency;
    private boolean customerActive;
    private boolean blockedAccount;
    private boolean hasManagerApproval;

    public InOperationRules() {
    }

    public InOperationRules(double amount, String currency, boolean customerActive,
                            boolean blockedAccount, boolean hasManagerApproval) {
        this.amount = amount;
        this.currency = currency;
        this.customerActive = customerActive;
        this.blockedAccount = blockedAccount;
        this.hasManagerApproval = hasManagerApproval;
    }

    // Getters y Setters
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isCustomerActive() { return customerActive; }
    public void setCustomerActive(boolean customerActive) { this.customerActive = customerActive; }

    public boolean isBlockedAccount() { return blockedAccount; }
    public void setBlockedAccount(boolean blockedAccount) { this.blockedAccount = blockedAccount; }

    public boolean isHasManagerApproval() { return hasManagerApproval; }
    public void setHasManagerApproval(boolean hasManagerApproval) { this.hasManagerApproval = hasManagerApproval; }
}
