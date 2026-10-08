package consolidation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import consolidation.model.InOperation;
import consolidation.model.OutOperation;

import java.math.BigDecimal;
import java.util.*;

public class ProcessorConsolidation {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String evaluateMovements(String inputJson) {
        if (inputJson == null || inputJson.isBlank()) {
            return "[]";
        }
        List<InOperation> movements;

        // aqui se realiza un tratamiento para el json de entrada
        try {
            movements = objectMapper.readValue(
                    inputJson,
                    new TypeReference<List<InOperation>>() {
                    }
            );
        } catch (JsonProcessingException e) {
            // JSON mal formado
            throw new IllegalArgumentException("JSON de movimientos inválido", e);
        }
        // El JSON literal "null" devuelve null.
        if (movements == null) {
            return "[]";
        }

        Set<String> operationIds = new HashSet<>();
        Map<String, OutOperation> operationsMap = new HashMap<>();

        for (InOperation operation : movements) {
            //Validacion de campo
            if (operation == null || operation.getId() == null
                    || operation.getAccountId() == null || operation.getAmount() == null) {
                continue;
            }

            // Regla 1. Si hay movimientos duplicados con el mismo id, solo debe considerarse el primero.
            if (!operationIds.add(operation.getId())) {
                continue;
            }

            // Regla 5. Movimientos con amount <= 0 se ignoran.
            if (operation.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // Reglas 3 y 4. Solo se procesan tipos conocidos; así no se crean
            // cuentas vacías.
            boolean isCredit = "CREDIT".equalsIgnoreCase(operation.getType());
            boolean isDebit = "DEBIT".equalsIgnoreCase(operation.getType());
            if (!isCredit && !isDebit) {
                continue;
            }
            OutOperation outOperation = operationsMap.computeIfAbsent(
                    operation.getAccountId(),
                    id -> new OutOperation(id)
            );

            if (isCredit) {
                outOperation.credit(operation.getAmount());
            } else {
                outOperation.debit(operation.getAmount());
            }
        }
        List<OutOperation> resultList = new ArrayList<>(operationsMap.values());

        //Regla 6. El resultado debe venir ordenado por accountId ascendente.
        resultList.sort(Comparator.comparing(OutOperation::getAccountId));

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultList);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el resultado", e);
        }


    }
}
