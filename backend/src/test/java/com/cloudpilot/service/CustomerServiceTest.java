package com.cloudpilot.service;

import com.cloudpilot.dto.Customer360Dto;
import com.cloudpilot.model.Customer;
import com.cloudpilot.model.Order;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.CustomerRepository;
import com.cloudpilot.repository.OrderRepository;
import com.cloudpilot.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AiClientService aiClientService;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository, orderRepository, ticketRepository, aiClientService);
    }

    @Test
    void testGetCustomer360_AggregatesSpendAndTickets() {
        ZonedDateTime now = ZonedDateTime.now();

        Order order1 = Order.builder().id(1L).amount(new BigDecimal("12000.00")).status("COMPLETED").createdAt(now.minusDays(10)).build();
        Order order2 = Order.builder().id(2L).amount(new BigDecimal("4500.00")).status("COMPLETED").createdAt(now.minusDays(2)).build();

        Customer customer = Customer.builder()
                .id(1L)
                .name("Acme Corp")
                .email("contact@acme.com")
                .createdAt(now.minusMonths(6))
                .build();

        Ticket openTicket = Ticket.builder().id(101L).subject("Open Bug").status(Ticket.Status.IN_PROGRESS).createdAt(now.minusHours(5)).build();
        Ticket resolvedTicket = Ticket.builder().id(102L).subject("Old Issue").status(Ticket.Status.RESOLVED).createdAt(now.minusDays(20)).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(order1, order2));
        when(ticketRepository.findByCustomerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(openTicket, resolvedTicket));
        when(aiClientService.getCustomerSummary(eq("Acme Corp"), any(), any())).thenReturn("Acme Corp is an active enterprise customer.");

        Customer360Dto dto = customerService.getCustomer360(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getCustomerId());
        assertEquals(new BigDecimal("16500.00"), dto.getTotalSpend());
        assertEquals(2, dto.getTotalOrders());
        assertEquals(1, dto.getOpenTicketsCount());
        assertEquals(1, dto.getResolvedTicketsCount());
        assertEquals(4, dto.getRecentActivity().size()); // 2 orders + 2 tickets
        assertEquals("Acme Corp is an active enterprise customer.", dto.getAiSummary());
    }
}
