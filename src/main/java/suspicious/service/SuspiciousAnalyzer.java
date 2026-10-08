package suspicious.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import suspicious.model.InOperations;
import suspicious.model.OutOperations;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class SuspiciousAnalyzer {
    private static final Duration FREQUENT_DEBITS_WINDOW = Duration.ofMinutes(5);
    private static final int FREQUENT_DEBITS_MIN_COUNT = 4;   // "más de 3"
    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal(50000);
    private static final int SHARED_DEVICE_MAX_ACCOUNTS = 2;  // "más de 2"

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public String evaluateRisksJson(String jsonInput) {
        if (jsonInput == null || jsonInput.trim().isEmpty()) {
            return "[]";
        }

        List<InOperations> events;
        try {
            events = objectMapper.readValue(jsonInput, new TypeReference<List<InOperations>>() {});
        } catch (JsonProcessingException e) {
            // Un JSON mal formado es un error, no "sin alertas".
            throw new IllegalArgumentException("JSON de eventos inválido", e);
        }

        try {
            return objectMapper.writeValueAsString(evaluateRisks(events));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el resultado", e);
        }
    }

    public List<OutOperations> evaluateRisks(List<InOperations> events) {
        List<OutOperations> alerts = new ArrayList<>();
        if (events == null || events.isEmpty()) return alerts;

        // Se descartan eventos sin los datos mínimos para evaluarse.
        List<InOperations> sortedEvents = events.stream()
                .filter(e -> e != null && e.getId() != null
                        && e.getAccountId() != null && e.getTimestamp() != null)
                .sorted(Comparator.comparing(InOperations::getTimestamp)
                        .thenComparing(InOperations::getId))
                .collect(Collectors.toList());

        alerts.addAll(checkTimeLimits(sortedEvents));
        alerts.addAll(checkExceededAmount(sortedEvents));
        alerts.addAll(checkSharedDevices(sortedEvents));

        return alerts;
    }

    private List<OutOperations> checkTimeLimits(List<InOperations> events) {
        List<OutOperations> alerts = new ArrayList<>();

        // TreeMap: cuentas en orden estable. Los eventos ya vienen ordenados por tiempo.
        Map<String, List<InOperations>> debitsByAccount = events.stream()
                .filter(e -> "DEBIT".equalsIgnoreCase(e.getType()))
                .collect(Collectors.groupingBy(InOperations::getAccountId, TreeMap::new, Collectors.toList()));

        for (Map.Entry<String, List<InOperations>> entry : debitsByAccount.entrySet()) {
            String accountId = entry.getKey();
            List<InOperations> accountDebits = entry.getValue();

            for (int i = 0; i < accountDebits.size(); i++) {
                InOperations startEvent = accountDebits.get(i);
                List<InOperations> windowEvents = new ArrayList<>();
                windowEvents.add(startEvent);

                for (int j = i + 1; j < accountDebits.size(); j++) {
                    InOperations currentEvent = accountDebits.get(j);
                    Duration elapsed = Duration.between(startEvent.getTimestamp(), currentEvent.getTimestamp());

                    // Ventana inclusiva: hasta exactamente 5 minutos cuenta.
                    if (elapsed.compareTo(FREQUENT_DEBITS_WINDOW) <= 0) {
                        windowEvents.add(currentEvent);
                    } else {
                        break;
                    }
                }

                if (windowEvents.size() >= FREQUENT_DEBITS_MIN_COUNT) {
                    List<String> eventIds = windowEvents.stream()
                            .map(InOperations::getId)
                            .collect(Collectors.toList());

                    alerts.add(new OutOperations("FREQUENT_DEBITS", accountId, eventIds));
                    // Salta los eventos ya reportados para no repetir la alerta
                    // con ventanas que se traslapan.
                    i += windowEvents.size() - 1;
                }
            }
        }
        return alerts;
    }

    private List<OutOperations> checkExceededAmount(List<InOperations> events) {
        List<OutOperations> alerts = new ArrayList<>();
        for (InOperations event : events) {
            // Regla 2: amount > 50000 (compareTo devuelve 1 si es mayor)
            if (event.getAmount() != null
                    && event.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
                alerts.add(new OutOperations("HIGH_AMOUNT", event.getAccountId(),
                        Collections.singletonList(event.getId())));
            }
        }
        return alerts;
    }

    private List<OutOperations> checkSharedDevices(List<InOperations> events) {
        List<OutOperations> alerts = new ArrayList<>();
        Map<String, List<InOperations>> eventsByDevice = events.stream()
                .filter(e -> e.getDeviceId() != null)
                .collect(Collectors.groupingBy(InOperations::getDeviceId, TreeMap::new, Collectors.toList()));

        for (Map.Entry<String, List<InOperations>> entry : eventsByDevice.entrySet()) {
            String deviceId = entry.getKey();
            List<InOperations> deviceEvents = entry.getValue();

            List<String> uniqueAccounts = deviceEvents.stream()
                    .map(InOperations::getAccountId)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            if (uniqueAccounts.size() > SHARED_DEVICE_MAX_ACCOUNTS) {
                List<String> eventIds = deviceEvents.stream()
                        .map(InOperations::getId)
                        .distinct()
                        .collect(Collectors.toList());

                alerts.add(new OutOperations("SHARED_DEVICE", deviceId, uniqueAccounts, eventIds));
            }
        }
        return alerts;
    }
}
