package com.cloudpilot.service;

import com.cloudpilot.dto.ClassificationDto;
import com.cloudpilot.dto.TicketRequestDto;
import com.cloudpilot.dto.TicketResponseDto;
import com.cloudpilot.exception.InvalidStatusTransitionException;
import com.cloudpilot.model.Customer;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.CustomerRepository;
import com.cloudpilot.repository.TicketHistoryRepository;
import com.cloudpilot.repository.TicketRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TicketHistoryRepository ticketHistoryRepository;

    @Mock
    private AiClientService aiClientService;

    @Mock
    private SlaService slaService;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private AuditLogService auditLogService;

    private MeterRegistry meterRegistry;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        ticketService = new TicketService(
                ticketRepository,
                customerRepository,
                ticketHistoryRepository,
                aiClientService,
                slaService,
                assignmentService,
                auditLogService,
                meterRegistry
        );
    }

    @Test
    void testCreateTicket_HappyPath() {
        Customer customer = Customer.builder().id(1L).name("Acme Corp").email("acme@corp.com").build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        ClassificationDto classification = ClassificationDto.builder()
                .category("Payments")
                .priority("HIGH")
                .sentiment("FRUSTRATED")
                .build();
        when(aiClientService.classify(any(), any())).thenReturn(classification);
        when(slaService.computeDeadline(eq(Ticket.Priority.HIGH), any())).thenReturn(ZonedDateTime.now().plusHours(2));

        Ticket savedTicket = Ticket.builder()
                .id(101L)
                .customer(customer)
                .subject("Billing Error")
                .description("Overcharge on invoice")
                .category("Payments")
                .priority(Ticket.Priority.HIGH)
                .status(Ticket.Status.NEW)
                .build();
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketRequestDto request = new TicketRequestDto(1L, "Billing Error", "Overcharge on invoice");
        TicketResponseDto response = ticketService.createTicket(request);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals("Payments", response.getCategory());
        assertEquals(Ticket.Priority.HIGH, response.getPriority());

        verify(assignmentService).assignTicket(any(Ticket.class));
        verify(ticketHistoryRepository).save(any());
    }

    @Test
    void testUpdateStatus_IllegalTransitionThrows() {
        Ticket ticket = Ticket.builder()
                .id(101L)
                .status(Ticket.Status.RESOLVED)
                .build();
        when(ticketRepository.findById(101L)).thenReturn(Optional.of(ticket));

        assertThrows(InvalidStatusTransitionException.class, () -> {
            // RESOLVED directly to NEW without reopening to IN_PROGRESS is disallowed
            ticketService.updateStatus(101L, Ticket.Status.NEW, "agent");
        });
    }
}
