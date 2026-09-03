package com.cloudpilot.service;

import com.cloudpilot.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SlaServiceTest {

    private SlaService slaService;

    @BeforeEach
    void setUp() {
        slaService = new SlaService();
    }

    @Test
    void testComputeDeadline_PerPriority() {
        ZonedDateTime now = ZonedDateTime.now();

        ZonedDateTime highDeadline = slaService.computeDeadline(Ticket.Priority.HIGH, now);
        ZonedDateTime medDeadline = slaService.computeDeadline(Ticket.Priority.MEDIUM, now);
        ZonedDateTime lowDeadline = slaService.computeDeadline(Ticket.Priority.LOW, now);

        assertEquals(now.plusHours(2), highDeadline);
        assertEquals(now.plusHours(8), medDeadline);
        assertEquals(now.plusHours(24), lowDeadline);
    }

    @Test
    void testCalculateRiskStatus_BreachedWhenPastDeadline() {
        Ticket ticket = Ticket.builder()
                .status(Ticket.Status.IN_PROGRESS)
                .slaDeadline(ZonedDateTime.now().minusMinutes(5))
                .build();

        Ticket.RiskStatus status = slaService.calculateRiskStatus(ticket);
        assertEquals(Ticket.RiskStatus.BREACHED, status);
    }

    @Test
    void testCalculateRiskStatus_AtRiskWhenLowRemaining() {
        ZonedDateTime created = ZonedDateTime.now().minusMinutes(110);
        ZonedDateTime deadline = ZonedDateTime.now().plusMinutes(10); // 10 mins remaining out of 120 (8.3% remaining)

        Ticket ticket = Ticket.builder()
                .status(Ticket.Status.IN_PROGRESS)
                .createdAt(created)
                .slaDeadline(deadline)
                .build();

        Ticket.RiskStatus status = slaService.calculateRiskStatus(ticket);
        assertEquals(Ticket.RiskStatus.AT_RISK, status);
    }

    @Test
    void testCalculateRiskStatus_OnTrack() {
        ZonedDateTime created = ZonedDateTime.now().minusMinutes(10);
        ZonedDateTime deadline = ZonedDateTime.now().plusMinutes(110);

        Ticket ticket = Ticket.builder()
                .status(Ticket.Status.NEW)
                .createdAt(created)
                .slaDeadline(deadline)
                .build();

        Ticket.RiskStatus status = slaService.calculateRiskStatus(ticket);
        assertEquals(Ticket.RiskStatus.ON_TRACK, status);
    }
}
