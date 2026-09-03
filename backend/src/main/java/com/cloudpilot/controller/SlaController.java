package com.cloudpilot.controller;

import com.cloudpilot.dto.TicketResponseDto;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.TicketRepository;
import com.cloudpilot.service.SlaService;
import com.cloudpilot.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sla")
@Tag(name = "SLA Engine", description = "SLA monitoring, at-risk feeds, and breach status")
public class SlaController {

    private final TicketRepository ticketRepository;
    private final SlaService slaService;
    private final TicketService ticketService;

    public SlaController(TicketRepository ticketRepository, SlaService slaService, TicketService ticketService) {
        this.ticketRepository = ticketRepository;
        this.slaService = slaService;
        this.ticketService = ticketService;
    }

    @GetMapping("/at-risk")
    @Operation(summary = "Get list of tickets flagged as AT_RISK or BREACHED")
    public ResponseEntity<List<TicketResponseDto>> getAtRiskTickets() {
        List<TicketResponseDto> atRisk = ticketRepository.findAtRiskOrBreachedTickets().stream()
                .map(ticketService::mapToDto)
                .toList();
        return ResponseEntity.ok(atRisk);
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "Get real-time SLA remaining time and risk status for a ticket")
    public ResponseEntity<Map<String, Object>> getTicketSla(@PathVariable Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket with ID " + ticketId + " not found"));

        Map<String, Object> slaInfo = new HashMap<>();
        slaInfo.put("ticketId", ticket.getId());
        slaInfo.put("priority", ticket.getPriority());
        slaInfo.put("slaDeadline", ticket.getSlaDeadline());
        slaInfo.put("remainingMinutes", slaService.getRemainingMinutes(ticket.getSlaDeadline()));
        slaInfo.put("riskStatus", slaService.calculateRiskStatus(ticket));

        return ResponseEntity.ok(slaInfo);
    }
}
