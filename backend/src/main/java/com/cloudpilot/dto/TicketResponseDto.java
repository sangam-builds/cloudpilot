package com.cloudpilot.dto;

import com.cloudpilot.model.Ticket;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
