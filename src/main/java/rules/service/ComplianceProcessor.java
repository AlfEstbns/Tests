package rules.service;

import rules.model.InOperationRules;

public class ComplianceProcessor {
    public boolean evaluateApproval(InOperationRules tx) {
        // Validación de null
        if (tx == null) {
            return false;
        }

        // 1. Regla de Monto Positivo
        if (tx.getAmount() <= 0) {
            return false;
        }

        // 2. Regla de Moneda Permitida (Solo MXN o USD)
        boolean isSupportedCurrency = "MXN".equalsIgnoreCase(tx.getCurrency())
                || "USD".equalsIgnoreCase(tx.getCurrency());
        if (!isSupportedCurrency) {
            return false;
        }

        // 3. Regla de Cliente Activo
        if (!tx.isCustomerActive()) {
            return false;
        }

        // 4. Regla de Cuenta Bloqueada
        if (tx.isBlockedAccount()) {
            return false;
        }

        // 5 y 6. Regla de Monto Alto (>= 100,000 requiere aprobación de manager)
        if (tx.getAmount() >= 100000) {
            return tx.isHasManagerApproval();
        }

        // Si superó todas las reglas base y el monto es menor a 100,000
        return true;
    }
}
