import consolidation.service.ProcessorConsolidation;
import rules.service.ComplianceProcessor;
import rules.model.InOperationRules;
import suspicious.service.SuspiciousAnalyzer;

public class Main {
    public static void main(String[] args) {
        System.out.println("   EJECUCIÓN DE EVALUACIÓN TÉCNICA JAVA   ");
        System.out.println("   EJECUCIÓN EJERCICIO 1   ");
        ejercicio1Consolidacion();

        System.out.println("   EJECUCIÓN EJERCICIO 2   ");
        ejercicio2Sospechosas();
        System.out.println("   EJECUCIÓN EJERCICIO 3   ");
        ejercicio3ReglasN();
    }

    private static void ejercicio1Consolidacion() {
        //JSON extraido del documento original
        String inputJson = """
                [ {"id": "M1", "accountId": "A1", "type": "DEBIT", "amount": 100, "currency": "MXN", "timestamp": "2026-07-13T10:00:00Z"}, {"id": "M2", "accountId": "A1", "type": "CREDIT", "amount": 250, "currency": "MXN", "timestamp": "2026-07-13T10:01:00Z"}, {"id": "M1", "accountId": "A1", "type": "DEBIT", "amount": 100, "currency": "MXN", "timestamp": "2026-07-13T10:02:00Z"}, {"id": "M3", "accountId": "A2", "type": "DEBIT", "amount": 50, "currency": "MXN", "timestamp": "2026-07-13T10:03:00Z"}, {"id": "M4", "accountId": "A2", "type": "CREDIT", "amount": -20, "currency": "MXN", "timestamp": "2026-07-13T10:04:00Z"} ]\s
                """;

        String outputJson = ProcessorConsolidation.evaluateConsolidation(inputJson);
        System.out.println(outputJson);
    }

    private static void ejercicio2Sospechosas() {
        String inputJson = """
                [ {"id": "E1", "accountId": "A1", "type": "DEBIT", "amount": 1000, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:00:00Z"}, {"id": "E2", "accountId": "A1", "type": "DEBIT", "amount": 1200, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:01:00Z"}, {"id": "E3", "accountId": "A1", "type": "DEBIT", "amount": 900, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:02:00Z"}, {"id": "E4", "accountId": "A1", "type": "DEBIT", "amount": 700, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:04:30Z"}, {"id": "E5", "accountId": "A2", "type": "CREDIT", "amount": 90000, "channel": "APP", "deviceId": "D2", "timestamp": "2026-07-13T10:05:00Z"}, {"id": "E6", "accountId": "A3", "type": "DEBIT", "amount": 100, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:06:00Z"}, {"id": "E7", "accountId": "A4", "type": "DEBIT", "amount": 200, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:07:00Z"}, {"id": "E8", "accountId": "A5", "type": "DEBIT", "amount": 300, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:08:00Z"} ]\s
                """;

        SuspiciousAnalyzer analyzer = new SuspiciousAnalyzer();

        // Ejecución pasando JSON y recibiendo JSON
        String jsonOutput = analyzer.evaluateRisksJson(inputJson);
        System.out.println(jsonOutput);
    }

    private static void ejercicio3ReglasN() {
        ComplianceProcessor processor = new ComplianceProcessor();

        // Caso 1: Transacción normal aprobada (< 100,000)
        InOperationRules tx1 = new InOperationRules(5000, "MXN", true, false, false);
        System.out.println("Caso 1 (Normal Aprobada): " + processor.evaluateApproval(tx1)); // true

        // Caso 2: Monto alto sin aprobación de manager
        InOperationRules tx2 = new InOperationRules(150000, "USD", true, false, false);
        System.out.println("Caso 2 (Monto Alto sin Manager): " + processor.evaluateApproval(tx2)); // false

        // Caso 3: Monto alto CON aprobación de manager
        InOperationRules tx3 = new InOperationRules(150000, "USD", true, false, true);
        System.out.println("Caso 3 (Monto Alto con Manager): " + processor.evaluateApproval(tx3)); // true

        // Caso 4: Cuenta bloqueada
        InOperationRules tx4 = new InOperationRules(1000, "MXN", true, true, false);
        System.out.println("Caso 4 (Cuenta Bloqueada): " + processor.evaluateApproval(tx4)); // false

        // Caso 5: Moneda no permitida (ej: EUR)
        InOperationRules tx5 = new InOperationRules(1000, "EUR", true, false, false);
        System.out.println("Caso 5 (Moneda EUR): " + processor.evaluateApproval(tx5)); // false
    }

}
