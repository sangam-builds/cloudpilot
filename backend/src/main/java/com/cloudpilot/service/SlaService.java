package com.cloudpilot.service;

import com.cloudpilot.model.Ticket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;

@Service
public class SlaService {

    public static final int SLA_HOURS_HIGH = 2;
    public static final int SLA_HOURS_MEDIUM = 8;
    public static final int SLA_HOURS_LOW = 24;

    /**
     * Compute SLA deadline based on priority:
     * HIGH = +2 hours, MEDIUM = +8 hours, LOW = +24 hours
     */
    public ZonedDateTime computeDeadline(Ticket.Priority priority, ZonedDateTime createdAt) {
        ZonedDateTime base = createdAt != null ? createdAt : ZonedDateTime.now();
        Ticket.Priority p = priority != null ? priority : Ticket.Priority.MEDIUM;

        return switch (p) {
            case HIGH -> base.plusHours(SLA_HOURS_HIGH);
            case MEDIUM -> base.plusHours(SLA_HOURS_MEDIUM);
            case LOW -> base.plusHours(SLA_HOURS_LOW);
        };
    }

    /**
     * Compute remaining minutes until SLA deadline. Negative value indicates breach.
     */
    public long getRemainingMinutes(ZonedDateTime slaDeadline) {
        if (slaDeadline == null) return 0;
        return Duration.between(ZonedDateTime.now(), slaDeadline).toMinutes();
    }

    /**
     * Determines the dynamic risk status of a ticket:
     * - BREACHED if now() is after slaDeadline
     * - AT_RISK if less than 20% of total SLA duration remains
     * - ON_TRACK otherwise
     */
    public Ticket.RiskStatus calculateRiskStatus(Ticket ticket) {
        if (ticket == null || ticket.getSlaDeadline() == null) {
            return Ticket.RiskStatus.ON_TRACK;
        }

        if (ticket.getStatus() == Ticket.Status.RESOLVED || ticket.getStatus() == Ticket.Status.CLOSED) {
            return ticket.getRiskStatus() != null ? ticket.getRiskStatus() : Ticket.RiskStatus.ON_TRACK;
        }

        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(ticket.getSlaDeadline())) {
            return Ticket.RiskStatus.BREACHED;
        }

        ZonedDateTime createdAt = ticket.getCreatedAt() != null ? ticket.getCreatedAt() : now.minusHours(1);
        long totalMinutes = Math.max(1, Duration.between(createdAt, ticket.getSlaDeadline()).toMinutes());
        long remainingMinutes = Duration.between(now, ticket.getSlaDeadline()).toMinutes();

        double ratio = (double) remainingMinutes / totalMinutes;
        if (ratio <= 0.20) {
            return Ticket.RiskStatus.AT_RISK;
        }

        return Ticket.RiskStatus.ON_TRACK;
    }
}
