package com.cloudpilot.dto;

import com.cloudpilot.model.Ticket;
import java.time.ZonedDateTime;

public class TicketResponseDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String subject;
    private String description;
    private String category;
    private Ticket.Priority priority;
    private Ticket.Sentiment sentiment;
    private Ticket.Status status;
    private Long assignedAgentId;
    private String assignedAgentName;
    private String assignedTeamName;
    private ZonedDateTime slaDeadline;
    private Long remainingMinutes;
    private Ticket.RiskStatus riskStatus;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public TicketResponseDto() {}

    public TicketResponseDto(Long id, Long customerId, String customerName, String customerEmail, String subject,
                             String description, String category, Ticket.Priority priority, Ticket.Sentiment sentiment,
                             Ticket.Status status, Long assignedAgentId, String assignedAgentName, String assignedTeamName,
                             ZonedDateTime slaDeadline, Long remainingMinutes, Ticket.RiskStatus riskStatus,
                             ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.subject = subject;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.sentiment = sentiment;
        this.status = status;
        this.assignedAgentId = assignedAgentId;
        this.assignedAgentName = assignedAgentName;
        this.assignedTeamName = assignedTeamName;
        this.slaDeadline = slaDeadline;
        this.remainingMinutes = remainingMinutes;
        this.riskStatus = riskStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TicketResponseDtoBuilder builder() { return new TicketResponseDtoBuilder(); }

    public static class TicketResponseDtoBuilder {
        private Long id;
        private Long customerId;
        private String customerName;
        private String customerEmail;
        private String subject;
        private String description;
        private String category;
        private Ticket.Priority priority;
        private Ticket.Sentiment sentiment;
        private Ticket.Status status;
        private Long assignedAgentId;
        private String assignedAgentName;
        private String assignedTeamName;
        private ZonedDateTime slaDeadline;
        private Long remainingMinutes;
        private Ticket.RiskStatus riskStatus;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        public TicketResponseDtoBuilder id(Long id) { this.id = id; return this; }
        public TicketResponseDtoBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public TicketResponseDtoBuilder customerName(String customerName) { this.customerName = customerName; return this; }
        public TicketResponseDtoBuilder customerEmail(String customerEmail) { this.customerEmail = customerEmail; return this; }
        public TicketResponseDtoBuilder subject(String subject) { this.subject = subject; return this; }
        public TicketResponseDtoBuilder description(String description) { this.description = description; return this; }
        public TicketResponseDtoBuilder category(String category) { this.category = category; return this; }
        public TicketResponseDtoBuilder priority(Ticket.Priority priority) { this.priority = priority; return this; }
        public TicketResponseDtoBuilder sentiment(Ticket.Sentiment sentiment) { this.sentiment = sentiment; return this; }
        public TicketResponseDtoBuilder status(Ticket.Status status) { this.status = status; return this; }
        public TicketResponseDtoBuilder assignedAgentId(Long assignedAgentId) { this.assignedAgentId = assignedAgentId; return this; }
        public TicketResponseDtoBuilder assignedAgentName(String assignedAgentName) { this.assignedAgentName = assignedAgentName; return this; }
        public TicketResponseDtoBuilder assignedTeamName(String assignedTeamName) { this.assignedTeamName = assignedTeamName; return this; }
        public TicketResponseDtoBuilder slaDeadline(ZonedDateTime slaDeadline) { this.slaDeadline = slaDeadline; return this; }
        public TicketResponseDtoBuilder remainingMinutes(Long remainingMinutes) { this.remainingMinutes = remainingMinutes; return this; }
        public TicketResponseDtoBuilder riskStatus(Ticket.RiskStatus riskStatus) { this.riskStatus = riskStatus; return this; }
        public TicketResponseDtoBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TicketResponseDtoBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public TicketResponseDto build() {
            return new TicketResponseDto(id, customerId, customerName, customerEmail, subject, description, category, priority, sentiment, status, assignedAgentId, assignedAgentName, assignedTeamName, slaDeadline, remainingMinutes, riskStatus, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Ticket.Priority getPriority() { return priority; }
    public void setPriority(Ticket.Priority priority) { this.priority = priority; }
    public Ticket.Sentiment getSentiment() { return sentiment; }
    public void setSentiment(Ticket.Sentiment sentiment) { this.sentiment = sentiment; }
    public Ticket.Status getStatus() { return status; }
    public void setStatus(Ticket.Status status) { this.status = status; }
    public Long getAssignedAgentId() { return assignedAgentId; }
    public void setAssignedAgentId(Long assignedAgentId) { this.assignedAgentId = assignedAgentId; }
    public String getAssignedAgentName() { return assignedAgentName; }
    public void setAssignedAgentName(String assignedAgentName) { this.assignedAgentName = assignedAgentName; }
    public String getAssignedTeamName() { return assignedTeamName; }
    public void setAssignedTeamName(String assignedTeamName) { this.assignedTeamName = assignedTeamName; }
    public ZonedDateTime getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(ZonedDateTime slaDeadline) { this.slaDeadline = slaDeadline; }
    public Long getRemainingMinutes() { return remainingMinutes; }
    public void setRemainingMinutes(Long remainingMinutes) { this.remainingMinutes = remainingMinutes; }
    public Ticket.RiskStatus getRiskStatus() { return riskStatus; }
    public void setRiskStatus(Ticket.RiskStatus riskStatus) { this.riskStatus = riskStatus; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
