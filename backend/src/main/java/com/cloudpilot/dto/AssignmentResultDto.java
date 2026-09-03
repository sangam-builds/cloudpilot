package com.cloudpilot.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResultDto implements Serializable {

    private Long ticketId;
    private Long agentId;
    private String agentName;
    private String teamName;
    private Double finalScore;
    private Map<String, Double> scoreBreakdown;
    private String status; // ASSIGNED or QUEUED
    private String message;
}
