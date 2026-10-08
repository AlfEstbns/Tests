package suspicious.service;

import suspicious.model.InOperations;
import suspicious.model.OutOperations;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class SuspiciousAnalyzer {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public String evaluateRisksJson(String jsonInput) {
        try {
            if (jsonInput == null || jsonInput.trim().isEmpty()) {
                return "[]";
            }

            // Deserializar JSON Input String -> List<InOperations>
            List<InOperations> events = objectMapper.readValue(
                    jsonInput,
                    new TypeReference<List<InOperations>>() {}
            );

            // Evaluar lógica de negocio
            List<OutOperations> alerts = evaluateRisks(events);

            // Serializar List<OutOperations> -> JSON Output String
            return objectMapper.writeValueAsString(alerts);

        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public List<OutOperations> evaluateRisks(List<InOperations> events) {
        List<OutOperations> alerts = new ArrayList<>();
        if (events == null || events.isEmpty()) return alerts;

        alerts.addAll(checkTimeLimits(events));
        alerts.addAll(checkExceededAmount(events));
        alerts.addAll(checkSharedDevices(events));

        return alerts;
    }

    private List<OutOperations> checkTimeLimits(List<InOperations> events) {
        List<OutOperations> alerts = new ArrayList<>();
        Map<String, List<InOperations>> debitsByAccount = events.stream()
                .filter(e -> "DEBIT".equalsIgnoreCase(e.getType()))
                .collect(Collectors.groupingBy(InOperations::getAccountId));

        for (Map.Entry<String, List<InOperations>> entry : debitsByAccount.entrySet()) {
            String accountId = entry.getKey();
            List<InOperations> accountDebits = entry.getValue();
            accountDebits.sort(Comparator.comparing(InOperations::getTimestamp));

            for (int i = 0; i < accountDebits.size(); i++) {
                List<InOperations> windowEvents = new ArrayList<>();
                InOperations startEvent = accountDebits.get(i);
                windowEvents.add(startEvent);

                for (int j = i + 1; j < accountDebits.size(); j++) {
                    InOperations currentEvent = accountDebits.get(j);
                    long secondsBetween = Duration.between(startEvent.getTimestamp(), currentEvent.getTimestamp()).getSeconds();

                    if (secondsBetween <= 300) {
                        windowEvents.add(currentEvent);
                    } else {
                        break;
                    }
                }

                if (windowEvents.size() >= 4) {
                    List<String> eventIds = windowEvents.stream()
                            .map(InOperations::getId)
                            .collect(Collectors.toList());

                    alerts.add(new OutOperations("FREQUENT_DEBITS", accountId, eventIds));
                    i += windowEvents.size() - 1;
                }
            }
        }
        return alerts;
    }

    private List<OutOperations> checkExceededAmount(List<InOperations> events) {
        List<OutOperations> alerts = new ArrayList<>();
        for (InOperations event : events) {
            if (event.getAmount() > 50000) {
                alerts.add(new OutOperations("HIGH_AMOUNT", event.getAccountId(), Collections.singletonList(event.getId())));
            }
        }
        return alerts;
    }

    private List<OutOperations> checkSharedDevices(List<InOperations> events) {
        List<OutOperations> alerts = new ArrayList<>();
        Map<String, List<InOperations>> eventsByDevice = events.stream()
                .filter(e -> e.getDeviceId() != null)
                .collect(Collectors.groupingBy(InOperations::getDeviceId));

        for (Map.Entry<String, List<InOperations>> entry : eventsByDevice.entrySet()) {
            String deviceId = entry.getKey();
            List<InOperations> deviceEvents = entry.getValue();

            List<String> uniqueAccounts = deviceEvents.stream()
                    .map(InOperations::getAccountId)
                    .distinct()
                    .collect(Collectors.toList());

            if (uniqueAccounts.size() > 2) {
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
