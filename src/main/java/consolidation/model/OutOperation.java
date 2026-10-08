package consolidation.model;

public class OutOperation {
    private String accountId;
    private double totalCredits;
    private double totalDebits;
    private double balance;
    private int validMovements;

    public OutOperation(String accountId) {
        this.accountId = accountId;
        this.totalCredits = 0.0;
        this.totalDebits = 0.0;
        this.balance = 0.0;
        this.validMovements = 0;
    }

    public void credit(double amount) {
        this.totalCredits += amount;
        this.validMovements++;
        calculate();
    }

    public void debit(double amount) {
        this.totalDebits += amount;
        this.validMovements++;
        calculate();
    }

    private void calculate() {
        this.balance = this.totalCredits - this.totalDebits;
    }


    public String getAccountId() {
        return accountId;
    }

    public double getTotalCredits() {
        return totalCredits;
    }

    public double getTotalDebits() {
        return totalDebits;
    }

    public double getBalance() {
        return balance;
    }

    public int getValidMovements() {
        return validMovements;
    }
}
