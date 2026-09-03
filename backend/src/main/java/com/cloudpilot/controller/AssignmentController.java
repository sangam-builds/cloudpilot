package com.cloudpilot.controller;

import com.cloudpilot.dto.AssignmentResultDto;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.TicketRepository;
import com.cloudpilot.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/assignment")
@Tag(name = "Assignment Engine", description = "Dynamic agent scoring algorithm and manual reassignment")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final TicketRepository ticketRepository;

    public AssignmentController(AssignmentService assignmentService, TicketRepository ticketRepository) {
        this.assignmentService = assignmentService;
        this.ticketRepository = ticketRepository;
    }

    @PostMapping("/assign/{ticketId}")
    @Operation(summary = "Run live agent scoring algorithm on a ticket and return factor score breakdown")
    public ResponseEntity<AssignmentResultDto> runAssignment(@PathVariable Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket with ID " + ticketId + " not found"));

        return ResponseEntity.ok(assignmentService.assignTicket(ticket));
    }

    @PostMapping("/reassign/{ticketId}")
    @Operation(summary = "Manually reassign ticket to specific agent with audit logging")
    public ResponseEntity<AssignmentResultDto> reassignTicket(
            @PathVariable Long ticketId,
            @RequestBody Map<String, Long> request,
            Principal principal
    ) {
        Long targetAgentId = request.get("agentId");
        if (targetAgentId == null) {
            return ResponseEntity.badRequest().build();
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket with ID " + ticketId + " not found"));

        String actor = principal != null ? principal.getName() : "Lead";
        return ResponseEntity.ok(assignmentService.reassignTicket(ticket, targetAgentId, actor));
    }
}
