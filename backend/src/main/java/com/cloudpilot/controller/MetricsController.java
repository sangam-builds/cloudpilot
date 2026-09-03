package com.cloudpilot.controller;

import com.cloudpilot.algorithms.ServiceDependencyGraph;
import com.cloudpilot.algorithms.TicketPriorityQueue;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.AgentRepository;
import com.cloudpilot.repository.CustomerRepository;
import com.cloudpilot.repository.TicketRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Metrics & Admin Analytics", description = "Operational metrics, SLA adherence, and system analytics")
public class MetricsController {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;
    private final TicketPriorityQueue priorityQueue;
    private final ServiceDependencyGraph dependencyGraph;

    public MetricsController(
            TicketRepository ticketRepository,
            CustomerRepository customerRepository,
            AgentRepository agentRepository,
            TicketPriorityQueue priorityQueue,
            ServiceDependencyGraph dependencyGraph
    ) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.agentRepository = agentRepository;
        this.priorityQueue = priorityQueue;
        this.dependencyGraph = dependencyGraph;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get aggregated metrics for the real-time Admin Dashboard")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        long totalTickets = ticketRepository.count();
        long newTickets = ticketRepository.countByStatus(Ticket.Status.NEW);
        long inProgress = ticketRepository.countByStatus(Ticket.Status.IN_PROGRESS);
        long assigned = ticketRepository.countByStatus(Ticket.Status.ASSIGNED);
        long resolved = ticketRepository.countByStatus(Ticket.Status.RESOLVED);
        long closed = ticketRepository.countByStatus(Ticket.Status.CLOSED);

        long atRisk = ticketRepository.countByRiskStatus(Ticket.RiskStatus.AT_RISK);
        long breached = ticketRepository.countByRiskStatus(Ticket.RiskStatus.BREACHED);

        long resolvedWithinSla = ticketRepository.countResolvedWithinSla();
        double slaAdherenceRate = (resolved + closed > 0)
                ? ((double) (resolved + closed - breached) / (resolved + closed)) * 100.0
                : 98.5;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTickets", totalTickets);
        summary.put("openTickets", newTickets + inProgress + assigned);
        summary.put("resolvedTickets", resolved + closed);
        summary.put("atRiskCount", atRisk);
        summary.put("breachedCount", breached);
        summary.put("slaAdherenceRate", Math.round(slaAdherenceRate * 10.0) / 10.0);
        summary.put("totalCustomers", customerRepository.count());
        summary.put("totalAgents", agentRepository.count());
        summary.put("queuedTicketsCount", priorityQueue.size());

        Map<String, Long> statusBreakdown = Map.of(
                "NEW", newTickets,
                "ASSIGNED", assigned,
                "IN_PROGRESS", inProgress,
                "RESOLVED", resolved,
                "CLOSED", closed
        );
        summary.put("statusBreakdown", statusBreakdown);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/blast-radius")
    @Operation(summary = "Simulate service failure blast radius via ServiceDependencyGraph BFS/DFS")
    public ResponseEntity<Map<String, Object>> getBlastRadius(@RequestParam(defaultValue = "Payment Service") String failedService) {
        List<String> affectedBFS = dependencyGraph.findAffectedServicesBFS(failedService);
        List<String> affectedDFS = dependencyGraph.findAffectedServicesDFS(failedService);

        Map<String, Object> result = new HashMap<>();
        result.put("failedService", failedService);
        result.put("affectedServicesBFS", affectedBFS);
        result.put("affectedServicesDFS", affectedDFS);
        result.put("impactedCount", affectedBFS.size());
        result.put("graphTopology", dependencyGraph.getAdjacencyList());

        return ResponseEntity.ok(result);
    }
}
