package com.cloudpilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Subject cannot be blank")
    private String subject;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    public TicketRequestDto() {}

    public TicketRequestDto(Long customerId, String subject, String description) {
        this.customerId = customerId;
        this.subject = subject;
        this.description = description;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
