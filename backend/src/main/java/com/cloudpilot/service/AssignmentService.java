package com.cloudpilot.service;

import com.cloudpilot.algorithms.AgentScorer;
import com.cloudpilot.algorithms.TicketPriorityQueue;
import com.cloudpilot.dto.AssignmentResultDto;
import com.cloudpilot.model.Agent;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.AgentRepository;
import com.cloudpilot.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;
    private final AgentScorer agentScorer;
    private final TicketPriorityQueue priorityQueue;
    private final AuditLogService auditLogService;

    /**
     * Automatic agent assignment pipeline:
     * 1. Fetches candidate agents in the matching team/department.
     * 2. Evaluates candidates using the multi-factor AgentScorer.
     * 3. Assigns top candidate, increments workload, and updates ticket.
     * 4. Fallback: pushes to TicketPriorityQueue if no agent is available.
     */
    @Transactional
    public AssignmentResultDto assignTicket(Ticket ticket) {
        if (ticket == null) {
            return AssignmentResultDto.builder().status("FAILED").message("Ticket cannot be null").build();
        }

        String category = ticket.getCategory() != null ? ticket.getCategory().toLowerCase() : "";
        List<Agent> candidates = findCandidateAgents(category);

        if (candidates.isEmpty()) {
            // Queue ticket in in-memory priority queue
            priorityQueue.push(ticket);
            log.warn("No available agents for category '{}'. Ticket #{} queued in PriorityQueue. Queue size: {}",
                    category, ticket.getId(), priorityQueue.size());

            auditLogService.log("system", "SYSTEM", "TICKET_QUEUED", "TICKET",
                    String.valueOf(ticket.getId()), "{\"reason\": \"No available agents for department " + category + "\"}");

            return AssignmentResultDto.builder()
                    .ticketId(ticket.getId())
                    .status("QUEUED")
                    .message("No immediate agent available. Ticket enqueued in priority schedule.")
                    .scoreBreakdown(Collections.emptyMap())
                    .build();
        }

        // Rank agents via AgentScorer O(n log n)
        List<AgentScorer.AgentScoreResult> ranked = agentScorer.rankAgents(candidates, ticket);
        AgentScorer.AgentScoreResult topMatch = ranked.get(0);
        Agent selectedAgent = topMatch.agent();

        // Increment agent workload & update ticket
        selectedAgent.setCurrentWorkload(selectedAgent.getCurrentWorkload() + 1);
        agentRepository.save(selectedAgent);

        ticket.setAssignedAgent(selectedAgent);
        ticket.setStatus(Ticket.Status.ASSIGNED);
        ticketRepository.save(ticket);

        log.info("Ticket #{} assigned to Agent '{}' (ID: {}) with score: {}",
                ticket.getId(), selectedAgent.getName(), selectedAgent.getId(), topMatch.totalScore());

        auditLogService.log("system", "SYSTEM", "AUTO_ASSIGN", "TICKET",
                String.valueOf(ticket.getId()), "{\"assignedAgentId\": " + selectedAgent.getId() + ", \"score\": " + topMatch.totalScore() + "}");

        return AssignmentResultDto.builder()
                .ticketId(ticket.getId())
                .agentId(selectedAgent.getId())
                .agentName(selectedAgent.getName())
                .teamName(selectedAgent.getTeam() != null ? selectedAgent.getTeam().getName() : "General")
                .finalScore(topMatch.totalScore())
                .scoreBreakdown(topMatch.breakdown())
                .status("ASSIGNED")
                .message("Successfully assigned to optimal agent via weighted scoring.")
                .build();
    }

    /**
     * Manually reassign a ticket to a specific agent (Team lead override).
     */
    @Transactional
    public AssignmentResultDto reassignTicket(Ticket ticket, Long targetAgentId, String actorId) {
        Agent targetAgent = agentRepository.findById(targetAgentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent with ID " + targetAgentId + " not found"));

        Agent previousAgent = ticket.getAssignedAgent();
        if (previousAgent != null && previousAgent.getCurrentWorkload() > 0) {
            previousAgent.setCurrentWorkload(previousAgent.getCurrentWorkload() - 1);
            agentRepository.save(previousAgent);
        }

        targetAgent.setCurrentWorkload(targetAgent.getCurrentWorkload() + 1);
        agentRepository.save(targetAgent);

        ticket.setAssignedAgent(targetAgent);
        ticket.setStatus(Ticket.Status.ASSIGNED);
        ticketRepository.save(ticket);

        AgentScorer.AgentScoreResult scoreResult = agentScorer.score(targetAgent, ticket);

        auditLogService.log(actorId, "LEAD", "MANUAL_REASSIGN", "TICKET",
                String.valueOf(ticket.getId()), "{\"reassignedTo\": " + targetAgent.getId() + ", \"previousAgent\": " + (previousAgent != null ? previousAgent.getId() : null) + "}");

        return AssignmentResultDto.builder()
                .ticketId(ticket.getId())
                .agentId(targetAgent.getId())
                .agentName(targetAgent.getName())
                .teamName(targetAgent.getTeam() != null ? targetAgent.getTeam().getName() : "General")
                .finalScore(scoreResult.totalScore())
                .scoreBreakdown(scoreResult.breakdown())
                .status("ASSIGNED")
                .message("Manual reassignment complete.")
                .build();
    }

    private List<Agent> findCandidateAgents(String category) {
        String query = category;
        if (category.contains("payment") || category.contains("billing") || category.contains("refund")) {
            query = "Payment";
        } else if (category.contains("tech") || category.contains("bug") || category.contains("api")) {
            query = "Technical";
        } else if (category.contains("ship") || category.contains("delivery") || category.contains("logistics")) {
            query = "Shipping";
        } else if (category.contains("account") || category.contains("security") || category.contains("sso")) {
            query = "Account";
        } else if (category.contains("return") || category.contains("rma")) {
            query = "Return";
        }

        List<Agent> matching = agentRepository.findAvailableAgentsByDepartment(query);
        if (matching.isEmpty()) {
            // If department-specific available agents are full, pull any available agent
            return agentRepository.findAll().stream().filter(Agent::getIsAvailable).toList();
        }
        return matching;
    }
}
