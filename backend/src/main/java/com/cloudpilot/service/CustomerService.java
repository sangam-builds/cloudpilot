package com.cloudpilot.service;

import com.cloudpilot.dto.Customer360Dto;
import com.cloudpilot.exception.TicketNotFoundException;
import com.cloudpilot.model.Customer;
import com.cloudpilot.model.Order;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.CustomerRepository;
import com.cloudpilot.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;
    private final AiClientService aiClientService;

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " does not exist."));
    }

    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    /**
     * Aggregates Customer 360 intelligence:
     * - Order volume and historical lifetime spend
     * - Open vs resolved ticket metrics
     * - Chronological unified timeline of orders and tickets
     * - Real-time AI sentiment & account health summary
     */
    @Transactional(readOnly = true)
    public Customer360Dto getCustomer360(Long customerId) {
        Customer customer = getCustomerById(customerId);

        List<Order> orders = customer.getOrders() != null ? customer.getOrders() : List.of();
        List<Ticket> tickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        BigDecimal totalSpend = orders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int openTickets = (int) tickets.stream()
                .filter(t -> t.getStatus() != Ticket.Status.RESOLVED && t.getStatus() != Ticket.Status.CLOSED)
                .count();

        int resolvedTickets = (int) tickets.stream()
                .filter(t -> t.getStatus() == Ticket.Status.RESOLVED || t.getStatus() == Ticket.Status.CLOSED)
                .count();

        // Build unified chronological activity timeline
        List<Customer360Dto.ActivityItemDto> activity = new ArrayList<>();

        for (Order order : orders) {
            activity.add(Customer360Dto.ActivityItemDto.builder()
                    .type("ORDER")
                    .id(order.getId())
                    .title("Order #" + order.getId())
                    .status(order.getStatus())
                    .amount(order.getAmount())
                    .timestamp(order.getCreatedAt())
                    .build());
        }

        for (Ticket ticket : tickets) {
            activity.add(Customer360Dto.ActivityItemDto.builder()
                    .type("TICKET")
                    .id(ticket.getId())
                    .title(ticket.getSubject())
                    .status(ticket.getStatus().name())
                    .amount(null)
                    .timestamp(ticket.getCreatedAt())
                    .build());
        }

        activity.sort((a, b) -> {
            if (a.getTimestamp() == null || b.getTimestamp() == null) return 0;
            return b.getTimestamp().compareTo(a.getTimestamp());
        });

        // Generate AI 360 summary
        List<String> ticketSubjects = tickets.stream().map(Ticket::getSubject).toList();
        List<String> orderSummaries = orders.stream()
                .map(o -> "$" + o.getAmount() + " (" + o.getStatus() + ")")
                .toList();

        String aiSummary = aiClientService.getCustomerSummary(customer.getName(), ticketSubjects, orderSummaries);

        return Customer360Dto.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .customerSince(customer.getCreatedAt())
                .totalOrders(orders.size())
                .totalSpend(totalSpend)
                .openTicketsCount(openTickets)
                .resolvedTicketsCount(resolvedTickets)
                .recentActivity(activity)
                .aiSummary(aiSummary)
                .build();
    }
}
