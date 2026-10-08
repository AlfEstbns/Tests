package consolidation.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import consolidation.model.InOperation;
import consolidation.model.OutOperation;

import java.util.*;

public class ProcessorConsolidation {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String evaluateConsolidation (String inputJson) {
        try {
            // Regla 2. Los eventos pueden venir desordenados, aqui se realiza un tratamiento para el json de entrada
            List<InOperation> movements = objectMapper.readValue(
                    inputJson,
                    new TypeReference<List<InOperation>>() {
                    }
            );

            Set<String> operationIds = new HashSet<>();
            Map<String, OutOperation> operationsMap = new HashMap<>();

            for (InOperation operation : movements) {
                // Regla 5. Movimientos con amount <= 0 deben ignorarse.
                if (operation.getAmount() <= 0) {
                    continue;
                }

                // Regla 1. Si hay movimientos duplicados con el mismo id, solo debe considerarse el primero.
                if (operationIds.contains(operation.getId())) {
                    continue;
                }
                operationIds.add(operation.getId());
                OutOperation outOperation = operationsMap.computeIfAbsent(
                        operation.getAccountId(),
                        id -> new OutOperation(id)
                );

                //Reglas 3. DEBIT resta al saldo. 4. CREDIT suma al saldo.
                if ("CREDIT".equalsIgnoreCase(operation.getType())) {
                    outOperation.credit(operation.getAmount());
                } else if ("DEBIT".equalsIgnoreCase(operation.getType())) {
                    outOperation.debit(operation.getAmount());
                }
            }
            List<OutOperation> resultList = new ArrayList<>(operationsMap.values());

            //Regla 6. El resultado debe venir ordenado por accountId ascendente.
            resultList.sort(Comparator.comparing(OutOperation::getAccountId));

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultList);

        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }

    }
}
