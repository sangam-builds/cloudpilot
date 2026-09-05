package com.cloudpilot.service;

import com.cloudpilot.dto.Customer360Dto;
import com.cloudpilot.model.Customer;
import com.cloudpilot.model.Order;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.repository.CustomerRepository;
import com.cloudpilot.repository.OrderRepository;
import com.cloudpilot.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final AiClientService aiClientService;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository, TicketRepository ticketRepository, AiClientService aiClientService) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.aiClientService = aiClientService;
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " does not exist."));
    }

    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @org.springframework.cache.annotation.Cacheable(value = "customer360", key = "#customerId", unless = "#result == null")
    @Transactional(readOnly = true)
    public Customer360Dto getCustomer360(Long customerId) {
        Customer customer = getCustomerById(customerId);

        List<Order> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
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
