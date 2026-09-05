package com.cloudpilot.service;

import com.cloudpilot.algorithms.AgentScorer;
import com.cloudpilot.algorithms.TicketPriorityQueue;
import com.cloudpilot.dto.AssignmentResultDto;
import com.cloudpilot.model.Agent;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.AgentRepository;
import com.cloudpilot.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AgentScorer agentScorer;

    @Mock
    private TicketPriorityQueue priorityQueue;

    @Mock
    private AuditLogService auditLogService;

    private AssignmentService assignmentService;

    @BeforeEach
    void setUp() {
        assignmentService = new AssignmentService(
                agentRepository,
                ticketRepository,
                agentScorer,
                priorityQueue,
                auditLogService
        );
    }

    @Test
    void testAssignTicket_SuccessfulAssignment() {
        Ticket ticket = Ticket.builder().id(1L).category("Payments").subject("Billing Issue").build();
        Agent agent = Agent.builder().id(10L).name("Alex Mercer").currentWorkload(1).rating(new BigDecimal("4.9")).isAvailable(true).build();

        when(agentRepository.findAvailableAgentsByDepartment("Payment")).thenReturn(List.of(agent));
        when(agentScorer.rankAgents(any(), eq(ticket))).thenReturn(List.of(
                new AgentScorer.AgentScoreResult(agent, 0.85, Map.of("skillMatch", 0.4))
        ));

        AssignmentResultDto result = assignmentService.assignTicket(ticket);

        assertNotNull(result);
        assertEquals("ASSIGNED", result.getStatus());
        assertEquals(10L, result.getAgentId());
        assertEquals("Alex Mercer", result.getAgentName());

        verify(agentRepository).save(argThat(a -> a.getCurrentWorkload() == 2));
        verify(ticketRepository).save(argThat(t -> t.getStatus() == Ticket.Status.ASSIGNED && t.getAssignedAgent().getId() == 10L));
    }

    @Test
    void testAssignTicket_NoAgentsAvailable_EnqueuesInPriorityQueue() {
        Ticket ticket = Ticket.builder().id(2L).category("Shipping & Logistics").build();

        when(agentRepository.findAvailableAgentsByDepartment("Shipping")).thenReturn(Collections.emptyList());
        when(agentRepository.findAll()).thenReturn(Collections.emptyList());

        AssignmentResultDto result = assignmentService.assignTicket(ticket);

        assertNotNull(result);
        assertEquals("QUEUED", result.getStatus());
        verify(priorityQueue).push(ticket);
        verify(ticketRepository, never()).save(any());
    }
}
