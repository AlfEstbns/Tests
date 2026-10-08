package consolidation.model;

import java.math.BigDecimal;

public class OutOperation {
    private String accountId;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
    private BigDecimal balance;
    private int validMovements;

    public OutOperation(String accountId) {
        this.accountId = accountId;
        this.totalCredits = BigDecimal.ZERO;
        this.totalDebits = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
        this.validMovements = 0;
    }

    public void credit(BigDecimal amount) {
        this.totalCredits = this.totalCredits.add(amount);
        this.validMovements++;
        calculate();
    }

    public void debit(BigDecimal amount) {
        this.totalDebits = this.totalDebits.add(amount);
        this.validMovements++;
        calculate();
    }

    private void calculate() {
        this.balance = this.totalCredits.subtract(this.totalDebits);
    }


    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getTotalCredits() {
        return totalCredits;
    }

    public BigDecimal getTotalDebits() {
        return totalDebits;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public int getValidMovements() {
        return validMovements;
    }
}
