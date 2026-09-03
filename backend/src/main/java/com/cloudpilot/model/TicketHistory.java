package com.cloudpilot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "ticket_history")
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "from_status", length = 30)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 30)
    private String toStatus;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private ZonedDateTime changedAt;

    public TicketHistory() {}

    public TicketHistory(Long id, Ticket ticket, String fromStatus, String toStatus, String changedBy, ZonedDateTime changedAt) {
        this.id = id;
        this.ticket = ticket;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public static TicketHistoryBuilder builder() { return new TicketHistoryBuilder(); }

    public static class TicketHistoryBuilder {
        private Long id;
        private Ticket ticket;
        private String fromStatus;
        private String toStatus;
        private String changedBy;
        private ZonedDateTime changedAt;

        public TicketHistoryBuilder id(Long id) { this.id = id; return this; }
        public TicketHistoryBuilder ticket(Ticket ticket) { this.ticket = ticket; return this; }
        public TicketHistoryBuilder fromStatus(String fromStatus) { this.fromStatus = fromStatus; return this; }
        public TicketHistoryBuilder toStatus(String toStatus) { this.toStatus = toStatus; return this; }
        public TicketHistoryBuilder changedBy(String changedBy) { this.changedBy = changedBy; return this; }
        public TicketHistoryBuilder changedAt(ZonedDateTime changedAt) { this.changedAt = changedAt; return this; }

        public TicketHistory build() {
            return new TicketHistory(id, ticket, fromStatus, toStatus, changedBy, changedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public ZonedDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(ZonedDateTime changedAt) { this.changedAt = changedAt; }
}
