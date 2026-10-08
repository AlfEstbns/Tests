package rules.service;

import rules.model.InOperationRules;

import java.util.Set;

public class ComplianceProcessor {
    private static final double MANAGER_APPROVAL_THRESHOLD = 100000;
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("MXN", "USD");

    public boolean evaluateApproval(InOperationRules tx) {
        // Validación de null
        if (tx == null) {
            return false;
        }

        // Reglas base: si alguna falla, se rechaza.
        if (!isAmountPositive(tx)               //Regla 1
                || !isCurrencySupported(tx)     //Regla 2
                || !tx.isCustomerActive()       //Regla 3
                || tx.isBlockedAccount()) {     //Regla 4
            return false;
        }

        //Regla 6 Montos altos requieren aprobación del manager.
        if (requiresManagerApproval(tx)) {
            return tx.isHasManagerApproval();
        }

        return true;

    }
    //1. El monto es mayor a 0.
    private boolean isAmountPositive(InOperationRules tx) {
        return tx.getAmount() > 0;
    }
    //2. La moneda es MXN o USD
    private boolean isCurrencySupported(InOperationRules tx) {
        return tx.getCurrency() != null
                && SUPPORTED_CURRENCIES.contains(tx.getCurrency().toUpperCase());
    }
    //6. Si el monto es igual o mayor a 100,000, requiere aprobación del manager.
    private boolean requiresManagerApproval(InOperationRules tx) {
        return tx.getAmount() >= MANAGER_APPROVAL_THRESHOLD;
    }
}
