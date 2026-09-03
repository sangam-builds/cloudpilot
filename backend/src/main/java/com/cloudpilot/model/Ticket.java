package com.cloudpilot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tickets")
public class Ticket {

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public enum Status {
        NEW, ASSIGNED, IN_PROGRESS, PENDING_CUSTOMER, RESOLVED, CLOSED
    }

    public enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE, FRUSTRATED
    }

    public enum RiskStatus {
        ON_TRACK, AT_RISK, BREACHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Sentiment sentiment = Sentiment.NEUTRAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Status status = Status.NEW;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_agent_id")
    private Agent assignedAgent;

    @Column(name = "sla_deadline")
    private ZonedDateTime slaDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_status", nullable = false, length = 30)
    private RiskStatus riskStatus = RiskStatus.ON_TRACK;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TicketHistory> history = new ArrayList<>();

    public Ticket() {}

    public Ticket(Long id, Customer customer, String subject, String description, String category,
                  Priority priority, Sentiment sentiment, Status status, Agent assignedAgent,
                  ZonedDateTime slaDeadline, RiskStatus riskStatus, ZonedDateTime createdAt,
                  ZonedDateTime updatedAt, List<TicketHistory> history) {
        this.id = id;
        this.customer = customer;
        this.subject = subject;
        this.description = description;
        this.category = category;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.sentiment = sentiment != null ? sentiment : Sentiment.NEUTRAL;
        this.status = status != null ? status : Status.NEW;
        this.assignedAgent = assignedAgent;
        this.slaDeadline = slaDeadline;
        this.riskStatus = riskStatus != null ? riskStatus : RiskStatus.ON_TRACK;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        if (history != null) this.history = history;
    }

    public static TicketBuilder builder() { return new TicketBuilder(); }

    public static class TicketBuilder {
        private Long id;
        private Customer customer;
        private String subject;
        private String description;
        private String category;
        private Priority priority = Priority.MEDIUM;
        private Sentiment sentiment = Sentiment.NEUTRAL;
        private Status status = Status.NEW;
        private Agent assignedAgent;
        private ZonedDateTime slaDeadline;
        private RiskStatus riskStatus = RiskStatus.ON_TRACK;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private List<TicketHistory> history = new ArrayList<>();

        public TicketBuilder id(Long id) { this.id = id; return this; }
        public TicketBuilder customer(Customer customer) { this.customer = customer; return this; }
        public TicketBuilder subject(String subject) { this.subject = subject; return this; }
        public TicketBuilder description(String description) { this.description = description; return this; }
        public TicketBuilder category(String category) { this.category = category; return this; }
        public TicketBuilder priority(Priority priority) { this.priority = priority; return this; }
        public TicketBuilder sentiment(Sentiment sentiment) { this.sentiment = sentiment; return this; }
        public TicketBuilder status(Status status) { this.status = status; return this; }
        public TicketBuilder assignedAgent(Agent assignedAgent) { this.assignedAgent = assignedAgent; return this; }
        public TicketBuilder slaDeadline(ZonedDateTime slaDeadline) { this.slaDeadline = slaDeadline; return this; }
        public TicketBuilder riskStatus(RiskStatus riskStatus) { this.riskStatus = riskStatus; return this; }
        public TicketBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TicketBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public TicketBuilder history(List<TicketHistory> history) { this.history = history; return this; }

        public Ticket build() {
            return new Ticket(id, customer, subject, description, category, priority, sentiment, status, assignedAgent, slaDeadline, riskStatus, createdAt, updatedAt, history);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Sentiment getSentiment() { return sentiment; }
    public void setSentiment(Sentiment sentiment) { this.sentiment = sentiment; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Agent getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(Agent assignedAgent) { this.assignedAgent = assignedAgent; }

    public ZonedDateTime getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(ZonedDateTime slaDeadline) { this.slaDeadline = slaDeadline; }

    public RiskStatus getRiskStatus() { return riskStatus; }
    public void setRiskStatus(RiskStatus riskStatus) { this.riskStatus = riskStatus; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<TicketHistory> getHistory() { return history; }
    public void setHistory(List<TicketHistory> history) { this.history = history; }
}
