package com.cloudpilot.scheduler;

import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.TicketRepository;
import com.cloudpilot.service.AuditLogService;
import com.cloudpilot.service.SlaService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlaMonitorJobTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private SlaService slaService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    private SlaMonitorJob slaMonitorJob;

    @BeforeEach
    void setUp() {
        slaMonitorJob = new SlaMonitorJob(ticketRepository, slaService, auditLogService, meterRegistry);
    }

    @Test
    void testScanSlaDeadlines_TransitionsToAtRisk() {
        ZonedDateTime now = ZonedDateTime.now();
        Ticket ticket = Ticket.builder()
                .id(1L)
                .subject("Payment Issue")
                .status(Ticket.Status.IN_PROGRESS)
                .priority(Ticket.Priority.HIGH)
                .riskStatus(Ticket.RiskStatus.ON_TRACK)
                .slaDeadline(now.plusMinutes(15))
                .createdAt(now.minusHours(1))
                .build();

        when(ticketRepository.findAll()).thenReturn(List.of(ticket));
        when(slaService.calculateRiskStatus(ticket)).thenReturn(Ticket.RiskStatus.AT_RISK);

        slaMonitorJob.scanSlaDeadlines();

        verify(ticketRepository).save(argThat(t -> t.getRiskStatus() == Ticket.RiskStatus.AT_RISK));
        verify(auditLogService, never()).log(any(), any(), eq("SLA_BREACHED"), any(), any(), any());
    }

    @Test
    void testScanSlaDeadlines_TransitionsToBreachedAndIncrementsMetric() {
        ZonedDateTime now = ZonedDateTime.now();
        Ticket ticket = Ticket.builder()
                .id(2L)
                .subject("System Outage")
                .status(Ticket.Status.IN_PROGRESS)
                .priority(Ticket.Priority.HIGH)
                .riskStatus(Ticket.RiskStatus.AT_RISK)
                .slaDeadline(now.minusMinutes(5))
                .createdAt(now.minusHours(2))
                .build();

        when(ticketRepository.findAll()).thenReturn(List.of(ticket));
        when(slaService.calculateRiskStatus(ticket)).thenReturn(Ticket.RiskStatus.BREACHED);
        when(meterRegistry.counter(eq("sla.breaches"), eq("priority"), eq("HIGH"))).thenReturn(counter);

        slaMonitorJob.scanSlaDeadlines();

        verify(ticketRepository).save(argThat(t -> t.getRiskStatus() == Ticket.RiskStatus.BREACHED));
        verify(counter).increment();
        verify(auditLogService).log(eq("sla-job"), eq("SYSTEM"), eq("SLA_BREACHED"), eq("TICKET"), eq("2"), any());
    }

    @Test
    void testScanSlaDeadlines_IgnoresResolvedTickets() {
        Ticket resolvedTicket = Ticket.builder()
                .id(3L)
                .status(Ticket.Status.RESOLVED)
                .riskStatus(Ticket.RiskStatus.ON_TRACK)
                .build();

        when(ticketRepository.findAll()).thenReturn(List.of(resolvedTicket));

        slaMonitorJob.scanSlaDeadlines();

        verify(ticketRepository, never()).save(any());
        verify(slaService, never()).calculateRiskStatus(any());
    }
}
