package com.cloudpilot.scheduler;

import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.TicketRepository;
import com.cloudpilot.service.AuditLogService;
import com.cloudpilot.service.SlaService;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Component
public class SlaMonitorJob {

    private static final Logger log = LoggerFactory.getLogger(SlaMonitorJob.class);

    private final TicketRepository ticketRepository;
    private final SlaService slaService;
    private final AuditLogService auditLogService;
    private final MeterRegistry meterRegistry;

    public SlaMonitorJob(TicketRepository ticketRepository, SlaService slaService, AuditLogService auditLogService, MeterRegistry meterRegistry) {
        this.ticketRepository = ticketRepository;
        this.slaService = slaService;
        this.auditLogService = auditLogService;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedRateString = "${cloudpilot.sla.scan-rate-ms:60000}")
    @Transactional
    public void scanSlaDeadlines() {
        List<Ticket> openTickets = ticketRepository.findAll().stream()
                .filter(t -> t.getStatus() != Ticket.Status.RESOLVED && t.getStatus() != Ticket.Status.CLOSED)
                .toList();

        if (openTickets.isEmpty()) {
            return;
        }

        int updatedCount = 0;
        int newBreaches = 0;

        for (Ticket ticket : openTickets) {
            Ticket.RiskStatus currentRisk = ticket.getRiskStatus();
            Ticket.RiskStatus calculatedRisk = slaService.calculateRiskStatus(ticket);

            if (currentRisk != calculatedRisk) {
                ticket.setRiskStatus(calculatedRisk);
                ticketRepository.save(ticket);
                updatedCount++;

                if (calculatedRisk == Ticket.RiskStatus.BREACHED) {
                    newBreaches++;
                    try {
                        meterRegistry.counter("sla.breaches", "priority", ticket.getPriority().name()).increment();
                    } catch (Exception ignored) {}

                    auditLogService.log("sla-job", "SYSTEM", "SLA_BREACHED", "TICKET",
                            String.valueOf(ticket.getId()), "{\"slaDeadline\": \"" + ticket.getSlaDeadline() + "\"}");

                    log.warn("SLA Breached on Ticket #{}: Subject '{}', Deadline: {}",
                            ticket.getId(), ticket.getSubject(), ticket.getSlaDeadline());
                } else if (calculatedRisk == Ticket.RiskStatus.AT_RISK) {
                    log.info("Ticket #{} flagged AT_RISK of SLA breach.", ticket.getId());
                }
            }
        }

        if (updatedCount > 0) {
            log.info("SLA Monitor completed: updated {} ticket(s), detected {} new breach(es).", updatedCount, newBreaches);
        }
    }
}
