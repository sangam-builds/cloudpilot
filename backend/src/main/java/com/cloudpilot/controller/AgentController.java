package com.cloudpilot.controller;

import com.cloudpilot.model.Agent;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.AgentRepository;
import com.cloudpilot.repository.TicketRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agents")
@Tag(name = "Agents", description = "Agent availability, workload, and team operations")
public class AgentController {

    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;

    public AgentController(AgentRepository agentRepository, TicketRepository ticketRepository) {
        this.agentRepository = agentRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    @Operation(summary = "List all support agents and workloads")
    public ResponseEntity<List<Agent>> listAgents() {
        return ResponseEntity.ok(agentRepository.findAll());
    }

    @GetMapping("/{id}/workload")
    @Operation(summary = "Get detailed workload breakdown for an agent")
    public ResponseEntity<Map<String, Object>> getAgentWorkload(@PathVariable Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent with ID " + id + " not found"));

        List<Ticket> activeTickets = ticketRepository.findByAssignedAgentId(id).stream()
                .filter(t -> t.getStatus() != Ticket.Status.RESOLVED && t.getStatus() != Ticket.Status.CLOSED)
                .toList();

        Map<String, Object> workload = new HashMap<>();
        workload.put("agentId", agent.getId());
        workload.put("agentName", agent.getName());
        workload.put("team", agent.getTeam() != null ? agent.getTeam().getName() : "General");
        workload.put("activeTicketCount", activeTickets.size());
        workload.put("isAvailable", agent.getIsAvailable());
        workload.put("rating", agent.getRating());
        workload.put("tickets", activeTickets);

        return ResponseEntity.ok(workload);
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Toggle agent availability status")
    public ResponseEntity<Agent> toggleAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request
    ) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent with ID " + id + " not found"));

        Boolean available = request.get("available");
        if (available != null) {
            agent.setIsAvailable(available);
            agentRepository.save(agent);
        }

        return ResponseEntity.ok(agent);
    }
}
