package com.cloudpilot.service;

import com.cloudpilot.dto.ClassificationDto;
import com.cloudpilot.dto.TicketRequestDto;
import com.cloudpilot.dto.TicketResponseDto;
import com.cloudpilot.exception.InvalidStatusTransitionException;
import com.cloudpilot.exception.TicketNotFoundException;
import com.cloudpilot.model.Customer;
import com.cloudpilot.model.Ticket;
import com.cloudpilot.model.TicketHistory;
import com.cloudpilot.repository.CustomerRepository;
import com.cloudpilot.repository.TicketHistoryRepository;
import com.cloudpilot.repository.TicketRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final AiClientService aiClientService;
    private final SlaService slaService;
    private final AssignmentService assignmentService;
    private final AuditLogService auditLogService;
    private final MeterRegistry meterRegistry;

    public TicketService(
            TicketRepository ticketRepository,
            CustomerRepository customerRepository,
            TicketHistoryRepository ticketHistoryRepository,
            AiClientService aiClientService,
            SlaService slaService,
            AssignmentService assignmentService,
            AuditLogService auditLogService,
            MeterRegistry meterRegistry
    ) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.aiClientService = aiClientService;
        this.slaService = slaService;
        this.assignmentService = assignmentService;
        this.auditLogService = auditLogService;
        this.meterRegistry = meterRegistry;
    }

    private static final Map<Ticket.Status, Set<Ticket.Status>> ALLOWED_TRANSITIONS = Map.of(
            Ticket.Status.NEW, Set.of(Ticket.Status.ASSIGNED, Ticket.Status.IN_PROGRESS, Ticket.Status.CLOSED),
            Ticket.Status.ASSIGNED, Set.of(Ticket.Status.IN_PROGRESS, Ticket.Status.PENDING_CUSTOMER, Ticket.Status.RESOLVED, Ticket.Status.CLOSED),
            Ticket.Status.IN_PROGRESS, Set.of(Ticket.Status.PENDING_CUSTOMER, Ticket.Status.RESOLVED, Ticket.Status.CLOSED),
            Ticket.Status.PENDING_CUSTOMER, Set.of(Ticket.Status.IN_PROGRESS, Ticket.Status.RESOLVED, Ticket.Status.CLOSED),
            Ticket.Status.RESOLVED, Set.of(Ticket.Status.CLOSED, Ticket.Status.IN_PROGRESS),
            Ticket.Status.CLOSED, Set.of(Ticket.Status.NEW)
    );

    @Transactional
    public TicketResponseDto createTicket(TicketRequestDto request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + request.getCustomerId() + " not found."));

        ClassificationDto classification = aiClientService.classify(request.getSubject(), request.getDescription());

        Ticket.Priority priority = Ticket.Priority.MEDIUM;
        try {
            if (classification.getPriority() != null) {
                priority = Ticket.Priority.valueOf(classification.getPriority().toUpperCase());
            }
        } catch (Exception ignored) {}

        Ticket.Sentiment sentiment = Ticket.Sentiment.NEUTRAL;
        try {
            if (classification.getSentiment() != null) {
                sentiment = Ticket.Sentiment.valueOf(classification.getSentiment().toUpperCase());
            }
        } catch (Exception ignored) {}

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime slaDeadline = slaService.computeDeadline(priority, now);

        Ticket ticket = Ticket.builder()
                .customer(customer)
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(classification.getCategory())
                .priority(priority)
                .sentiment(sentiment)
                .status(Ticket.Status.NEW)
                .slaDeadline(slaDeadline)
                .riskStatus(Ticket.RiskStatus.ON_TRACK)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        assignmentService.assignTicket(savedTicket);

        ticketHistoryRepository.save(TicketHistory.builder()
                .ticket(savedTicket)
                .fromStatus(null)
                .toStatus(Ticket.Status.NEW.name())
                .changedBy("Customer: " + customer.getName())
                .build());

        try {
            meterRegistry.counter("tickets.created", "priority", priority.name(), "category", classification.getCategory()).increment();
        } catch (Exception ignored) {}

        auditLogService.log(String.valueOf(customer.getId()), "CUSTOMER", "CREATE_TICKET", "TICKET",
                String.valueOf(savedTicket.getId()), "{\"priority\": \"" + priority + "\", \"category\": \"" + classification.getCategory() + "\"}");

        return mapToDto(savedTicket);
    }

    @Transactional(readOnly = true)
    public TicketResponseDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        return mapToDto(ticket);
    }

    @Transactional(readOnly = true)
    public Page<TicketResponseDto> getTickets(String status, String priority, Long customerId, Long agentId, Pageable pageable) {
        Specification<Ticket> spec = (root, query, cb) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), Ticket.Status.valueOf(status.toUpperCase())));
            }
            if (priority != null && !priority.isBlank()) {
                predicates.add(cb.equal(root.get("priority"), Ticket.Priority.valueOf(priority.toUpperCase())));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }
            if (agentId != null) {
                predicates.add(cb.equal(root.get("assignedAgent").get("id"), agentId));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return ticketRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional
    public TicketResponseDto updateStatus(Long ticketId, Ticket.Status newStatus, String actorId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        Ticket.Status oldStatus = ticket.getStatus();

        if (oldStatus == newStatus) {
            return mapToDto(ticket);
        }

        Set<Ticket.Status> allowed = ALLOWED_TRANSITIONS.getOrDefault(oldStatus, Collections.emptySet());
        if (!allowed.contains(newStatus)) {
            throw new InvalidStatusTransitionException(oldStatus.name(), newStatus.name());
        }

        ticket.setStatus(newStatus);
        if (newStatus == Ticket.Status.RESOLVED) {
            try {
                meterRegistry.counter("tickets.resolved").increment();
            } catch (Exception ignored) {}
        }

        Ticket updated = ticketRepository.save(ticket);

        ticketHistoryRepository.save(TicketHistory.builder()
                .ticket(ticket)
                .fromStatus(oldStatus.name())
                .toStatus(newStatus.name())
                .changedBy(actorId != null ? actorId : "System")
                .build());

        auditLogService.log(actorId, "USER", "STATUS_CHANGE", "TICKET",
                String.valueOf(ticketId), "{\"from\": \"" + oldStatus + "\", \"to\": \"" + newStatus + "\"}");

        return mapToDto(updated);
    }

    @Transactional
    public void addComment(Long ticketId, String commentText, String author) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        auditLogService.log(author, "USER", "ADD_COMMENT", "TICKET",
                String.valueOf(ticketId), "{\"comment\": \"" + commentText.replace("\"", "'") + "\"}");
    }

    public TicketResponseDto mapToDto(Ticket ticket) {
        long remaining = slaService.getRemainingMinutes(ticket.getSlaDeadline());

        return TicketResponseDto.builder()
                .id(ticket.getId())
                .customerId(ticket.getCustomer() != null ? ticket.getCustomer().getId() : null)
                .customerName(ticket.getCustomer() != null ? ticket.getCustomer().getName() : "Unknown")
                .customerEmail(ticket.getCustomer() != null ? ticket.getCustomer().getEmail() : "")
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .sentiment(ticket.getSentiment())
                .status(ticket.getStatus())
                .assignedAgentId(ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getId() : null)
                .assignedAgentName(ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getName() : "Unassigned")
                .assignedTeamName(ticket.getAssignedAgent() != null && ticket.getAssignedAgent().getTeam() != null ? ticket.getAssignedAgent().getTeam().getName() : "Unassigned")
                .slaDeadline(ticket.getSlaDeadline())
                .remainingMinutes(remaining)
                .riskStatus(ticket.getRiskStatus())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
