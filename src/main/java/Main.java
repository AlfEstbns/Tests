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
        //String inputJson = "null";

        String outputJson = ProcessorConsolidation.evaluateMovements(inputJson);
        System.out.println(outputJson);
    }

    private static void ejercicio2Sospechosas() {
        /// H A P P Y   P A T H
        String inputJson = """
                [ {"id": "E1", "accountId": "A1", "type": "DEBIT", "amount": 1000, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:00:00Z"}, {"id": "E2", "accountId": "A1", "type": "DEBIT", "amount": 1200, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:01:00Z"}, {"id": "E3", "accountId": "A1", "type": "DEBIT", "amount": 900, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:02:00Z"}, {"id": "E4", "accountId": "A1", "type": "DEBIT", "amount": 700, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:04:30Z"}, {"id": "E5", "accountId": "A2", "type": "CREDIT", "amount": 90000, "channel": "APP", "deviceId": "D2", "timestamp": "2026-07-13T10:05:00Z"}, {"id": "E6", "accountId": "A3", "type": "DEBIT", "amount": 100, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:06:00Z"}, {"id": "E7", "accountId": "A4", "type": "DEBIT", "amount": 200, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:07:00Z"}, {"id": "E8", "accountId": "A5", "type": "DEBIT", "amount": 300, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:08:00Z"} ]\s
                """;

        ///CASO PRUEBA original pero Desordenado
        String shuffledJson = """
                [ {"id": "E8", "accountId": "A5", "type": "DEBIT", "amount": 300, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:08:00Z"},
                  {"id": "E3", "accountId": "A1", "type": "DEBIT", "amount": 900, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:02:00Z"},
                  {"id": "E6", "accountId": "A3", "type": "DEBIT", "amount": 100, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:06:00Z"},
                  {"id": "E1", "accountId": "A1", "type": "DEBIT", "amount": 1000, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:00:00Z"},
                  {"id": "E5", "accountId": "A2", "type": "CREDIT", "amount": 90000, "channel": "APP", "deviceId": "D2", "timestamp": "2026-07-13T10:05:00Z"},
                  {"id": "E4", "accountId": "A1", "type": "DEBIT", "amount": 700, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:04:30Z"},
                  {"id": "E7", "accountId": "A4", "type": "DEBIT", "amount": 200, "channel": "WEB", "deviceId": "D9", "timestamp": "2026-07-13T10:07:00Z"},
                  {"id": "E2", "accountId": "A1", "type": "DEBIT", "amount": 1200, "channel": "WEB", "deviceId": "D1", "timestamp": "2026-07-13T10:01:00Z"} ]
                """;

        /// CASO PRUEBA  limites de las reglas
        String boundariesJson = """
                [ {"id": "B1", "accountId": "A1", "type": "DEBIT", "amount": 10, "deviceId": "X1", "timestamp": "2026-07-13T10:00:00Z"},
                  {"id": "B2", "accountId": "A1", "type": "DEBIT", "amount": 10, "deviceId": "X1", "timestamp": "2026-07-13T10:01:00Z"},
                  {"id": "B3", "accountId": "A1", "type": "DEBIT", "amount": 10, "deviceId": "X1", "timestamp": "2026-07-13T10:02:00Z"},
                  {"id": "B4", "accountId": "A1", "type": "DEBIT", "amount": 10, "deviceId": "X1", "timestamp": "2026-07-13T10:05:00Z"},
                  {"id": "C1", "accountId": "A2", "type": "DEBIT", "amount": 10, "deviceId": "X2", "timestamp": "2026-07-13T10:00:00Z"},
                  {"id": "C2", "accountId": "A2", "type": "DEBIT", "amount": 10, "deviceId": "X2", "timestamp": "2026-07-13T10:01:00Z"},
                  {"id": "C3", "accountId": "A2", "type": "DEBIT", "amount": 10, "deviceId": "X2", "timestamp": "2026-07-13T10:02:00Z"},
                  {"id": "C4", "accountId": "A2", "type": "DEBIT", "amount": 10, "deviceId": "X2", "timestamp": "2026-07-13T10:05:01Z"},
                  {"id": "H1", "accountId": "A3", "type": "CREDIT", "amount": 50000, "deviceId": "X3", "timestamp": "2026-07-13T11:00:00Z"},
                  {"id": "H2", "accountId": "A4", "type": "CREDIT", "amount": 50000.01, "deviceId": "X4", "timestamp": "2026-07-13T11:00:00Z"},
                  {"id": "S1", "accountId": "A5", "type": "CREDIT", "amount": 5, "deviceId": "X5", "timestamp": "2026-07-13T12:00:00Z"},
                  {"id": "S2", "accountId": "A6", "type": "CREDIT", "amount": 5, "deviceId": "X5", "timestamp": "2026-07-13T12:01:00Z"} ]
                """;

        SuspiciousAnalyzer analyzer = new SuspiciousAnalyzer();

        // DIFERENTES CASOS
        System.out.println(analyzer.evaluateRisksJson(inputJson));
        //System.out.println(analyzer.evaluateRisksJson(shuffledJson));
        //System.out.println(analyzer.evaluateRisksJson(boundariesJson));
        //System.out.println(analyzer.evaluateRisksJson(""));
        //System.out.println(analyzer.evaluateRisksJson("[]"));
        //System.out.println(analyzer.evaluateRisksJson(null));

    }

    private static void ejercicio3ReglasN() {
        ComplianceProcessor processor = new ComplianceProcessor();
        ///H A P P Y   P A T H
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
        System.out.println("Caso 5 (Moneda EUR): " + processor.evaluateApproval(tx5));// false

        ///CASOS DE PRUEBA ESTRICTOS
        // Caso 6: Monto cero
        InOperationRules tx6 = new InOperationRules(0, "MXN", true, false, false);
        System.out.println("Caso 6 (Monto 0): " + processor.evaluateApproval(tx6)); // false

        // Caso 7: Monto negativo
        InOperationRules tx7 = new InOperationRules(-500, "MXN", true, false, false);
        System.out.println("Caso 7 (Monto negativo): " + processor.evaluateApproval(tx7)); // false

        // Caso 8: Moneda nula
        InOperationRules tx8 = new InOperationRules(1000, null, true, false, false);
        System.out.println("Caso 8 (Moneda null): " + processor.evaluateApproval(tx8)); // false

        // Caso 9: Moneda en minúsculas
        InOperationRules tx9 = new InOperationRules(1000, "mxn", true, false, false);
        System.out.println("Caso 9 (Moneda minúsculas): " + processor.evaluateApproval(tx9)); // true

        // Caso 10: Cliente inactivo
        InOperationRules tx10 = new InOperationRules(1000, "MXN", false, false, false);
        System.out.println("Caso 10 (Cliente inactivo): " + processor.evaluateApproval(tx10)); // false

        // Caso 11: Justo debajo del umbral
        InOperationRules tx11 = new InOperationRules(99999.99, "MXN", true, false, false);
        System.out.println("Caso 11 (99,999.99): " + processor.evaluateApproval(tx11)); // true

        // Caso 12: Exactamente en el umbral, sin manager
        InOperationRules tx12 = new InOperationRules(100000, "MXN", true, false, false);
        System.out.println("Caso 12 (100,000 sin manager): " + processor.evaluateApproval(tx12)); // false

        // Caso 13: Exactamente en el umbral, con manager
        InOperationRules tx13 = new InOperationRules(100000, "MXN", true, false, true);
        System.out.println("Caso 13 (100,000 con manager): " + processor.evaluateApproval(tx13)); // true

        // Caso 14: Monto alto con manager, pero cuenta bloqueada
        InOperationRules tx14 = new InOperationRules(150000, "USD", true, true, true);
        System.out.println("Caso 14 (Manager + bloqueada): " + processor.evaluateApproval(tx14)); // false

        // Caso 15: Transacción nula
        System.out.println("Caso 15 (null): " + processor.evaluateApproval(null)); // false
    }

}
