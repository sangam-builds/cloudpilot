package com.cloudpilot.controller;

import com.cloudpilot.dto.TicketRequestDto;
import com.cloudpilot.dto.TicketResponseDto;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.service.AiClientService;
import com.cloudpilot.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Ticket creation, filtering, status transitions, and comments")
public class TicketController {

    private final TicketService ticketService;
    private final AiClientService aiClientService;

    public TicketController(TicketService ticketService, AiClientService aiClientService) {
        this.ticketService = ticketService;
        this.aiClientService = aiClientService;
    }

    @PostMapping
    @Operation(summary = "Create ticket, trigger AI classification, SLA scheduling, and agent assignment")
    public ResponseEntity<TicketResponseDto> createTicket(@Valid @RequestBody TicketRequestDto request) {
        TicketResponseDto response = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full ticket details by ID")
    public ResponseEntity<TicketResponseDto> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping
    @Operation(summary = "Query tickets with optional filtering and pagination")
    public ResponseEntity<Page<TicketResponseDto>> listTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(ticketService.getTickets(status, priority, customerId, agentId, PageRequest.of(page, size, sort)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update ticket status with strict transition validation")
    public ResponseEntity<TicketResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Principal principal
    ) {
        String statusStr = request.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Ticket.Status newStatus = Ticket.Status.valueOf(statusStr.toUpperCase());
        String actor = principal != null ? principal.getName() : "Agent";

        return ResponseEntity.ok(ticketService.updateStatus(id, newStatus, actor));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to ticket thread")
    public ResponseEntity<Map<String, String>> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Principal principal
    ) {
        String comment = request.get("comment");
        String author = principal != null ? principal.getName() : "Customer";
        ticketService.addComment(id, comment, author);
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Comment added successfully"));
    }

    @GetMapping("/{id}/suggest-reply")
    @Operation(summary = "Get AI-generated suggested draft reply via RAG")
    public ResponseEntity<Map<String, String>> getSuggestedReply(@PathVariable Long id) {
        TicketResponseDto ticket = ticketService.getTicketById(id);
        String suggestion = aiClientService.suggestReply(ticket.getSubject(), ticket.getDescription());
        return ResponseEntity.ok(Map.of("suggestedReply", suggestion));
    }
}
