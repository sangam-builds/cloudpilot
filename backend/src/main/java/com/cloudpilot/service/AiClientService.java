package com.cloudpilot.service;

import com.cloudpilot.dto.ClassificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
public class AiClientService {

    private static final Logger log = LoggerFactory.getLogger(AiClientService.class);

    private final RestTemplate restTemplate;
    private final String aiServiceBaseUrl;

    public AiClientService(
            RestTemplateBuilder builder,
            @Value("${cloudpilot.ai-service.base-url:http://localhost:8000}") String aiServiceBaseUrl,
            @Value("${cloudpilot.ai-service.timeout-ms:5000}") long timeoutMs
    ) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
        this.aiServiceBaseUrl = aiServiceBaseUrl;
    }

    public ClassificationDto classify(String subject, String description) {
        String endpoint = aiServiceBaseUrl + "/classify";
        try {
            Map<String, String> request = Map.of(
                    "subject", subject != null ? subject : "",
                    "description", description != null ? description : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ClassificationDto> response = executeWithRetry(endpoint, entity, ClassificationDto.class);
            if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("AI Service classified ticket successfully: category={}, priority={}",
                        response.getBody().getCategory(), response.getBody().getPriority());
                return response.getBody();
            }
        } catch (Exception ex) {
            log.warn("AI Service classify failed (fallback active): {}", formatError(ex));
        }

        return ruleBasedFallbackClassifier(subject, description);
    }

    public String getCustomerSummary(String customerName, List<String> ticketSubjects, List<String> orderSummaries) {
        String endpoint = aiServiceBaseUrl + "/customer-summary";
        try {
            Map<String, Object> payload = Map.of(
                    "customer_name", customerName != null ? customerName : "Customer",
                    "ticket_history", ticketSubjects != null ? ticketSubjects : List.of(),
                    "order_history", orderSummaries != null ? orderSummaries : List.of()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = executeWithRetry(endpoint, entity, Map.class);
            if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().containsKey("summary")) {
                return (String) response.getBody().get("summary");
            }
        } catch (Exception ex) {
            log.warn("AI Customer Summary failed (fallback active): {}", formatError(ex));
        }

        return String.format("%s has %d recorded support ticket(s) and %d order transaction(s). Account is active and in good standing.",
                customerName, ticketSubjects != null ? ticketSubjects.size() : 0, orderSummaries != null ? orderSummaries.size() : 0);
    }

    public String suggestReply(String subject, String description) {
        String endpoint = aiServiceBaseUrl + "/suggest-reply";
        try {
            Map<String, String> payload = Map.of(
                    "subject", subject != null ? subject : "",
                    "description", description != null ? description : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = executeWithRetry(endpoint, entity, Map.class);
            if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().containsKey("suggested_reply")) {
                return (String) response.getBody().get("suggested_reply");
            }
        } catch (Exception ex) {
            log.warn("AI Suggested Reply failed (fallback active): {}", formatError(ex));
        }

        return "Thank you for reaching out to CloudPilot Support. We have received your inquiry and our engineering team is actively investigating. We will update you shortly.";
    }

    private <T> ResponseEntity<T> executeWithRetry(String endpoint, HttpEntity<?> entity, Class<T> responseType) {
        try {
            return restTemplate.postForEntity(endpoint, entity, responseType);
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            int code = ex.getStatusCode().value();
            // Retry once on gateway/cold-start issues (502/503/504)
            if (code == 502 || code == 503 || code == 504) {
                try {
                    Thread.sleep(1000);
                    return restTemplate.postForEntity(endpoint, entity, responseType);
                } catch (Exception ignored) {
                    throw ex;
                }
            }
            throw ex;
        }
    }

    private String formatError(Exception ex) {
        if (ex instanceof org.springframework.web.client.HttpStatusCodeException httpEx) {
            return httpEx.getStatusCode().value() + " " + httpEx.getStatusText();
        }
        String msg = ex.getMessage();
        if (msg == null) return ex.getClass().getSimpleName();
        if (msg.contains("<!DOCTYPE") || msg.contains("<html") || msg.length() > 120) {
            if (msg.contains("502")) return "502 Bad Gateway (Service waking up or unreachable)";
            if (msg.contains("503")) return "503 Service Unavailable";
            if (msg.contains("504")) return "504 Gateway Timeout";
            return msg.substring(0, Math.min(msg.length(), 100)) + "...";
        }
        return msg;
    }

    public ClassificationDto ruleBasedFallbackClassifier(String subject, String description) {
        String combined = ((subject != null ? subject : "") + " " + (description != null ? description : "")).toLowerCase();

        String category = "Technical Support";
        String priority = "MEDIUM";
        String sentiment = "NEUTRAL";
        String department = "Technical Support";

        if (combined.contains("refund") || combined.contains("charge") || combined.contains("invoice") || combined.contains("billing") || combined.contains("card") || combined.contains("double")) {
            category = "Payments";
            department = "Payments & Billing";
        } else if (combined.contains("delivery") || combined.contains("ship") || combined.contains("fedex") || combined.contains("dhl") || combined.contains("tracking") || combined.contains("customs")) {
            category = "Shipping & Logistics";
            department = "Shipping & Logistics";
        } else if (combined.contains("sso") || combined.contains("2fa") || combined.contains("saml") || combined.contains("password") || combined.contains("security") || combined.contains("soc2")) {
            category = "Account & Security";
            department = "Account & Security";
        } else if (combined.contains("rma") || combined.contains("broken") || combined.contains("damaged") || combined.contains("return")) {
            category = "Returns & Refunds";
            department = "Returns & Refunds";
        }

        if (combined.contains("urgent") || combined.contains("down") || combined.contains("outage") || combined.contains("504") || combined.contains("double charge") || combined.contains("breached")) {
            priority = "HIGH";
        } else if (combined.contains("minor") || combined.contains("feedback") || combined.contains("seat") || combined.contains("question")) {
            priority = "LOW";
        }

        if (combined.contains("frustrated") || combined.contains("unacceptable") || combined.contains("broken") || combined.contains("error")) {
            sentiment = "FRUSTRATED";
        } else if (combined.contains("thanks") || combined.contains("great") || combined.contains("appreciate")) {
            sentiment = "POSITIVE";
        }

        return ClassificationDto.builder()
                .category(category)
                .priority(priority)
                .sentiment(sentiment)
                .department(department)
                .confidence(0.85)
                .extractedKeywords(List.of("fallback", "heuristic-match"))
                .rationale("Classified via Java resilient keyword fallback engine.")
                .build();
    }
}
