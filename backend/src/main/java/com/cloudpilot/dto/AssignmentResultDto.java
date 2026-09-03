package com.cloudpilot.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AssignmentResultDto implements Serializable {

    private Long ticketId;
    private Long agentId;
    private String agentName;
    private String teamName;
    private Double finalScore;
    private Map<String, Double> scoreBreakdown = new HashMap<>();
    private String status;
    private String message;

    public AssignmentResultDto() {}

    public AssignmentResultDto(Long ticketId, Long agentId, String agentName, String teamName, Double finalScore, Map<String, Double> scoreBreakdown, String status, String message) {
        this.ticketId = ticketId;
        this.agentId = agentId;
        this.agentName = agentName;
        this.teamName = teamName;
        this.finalScore = finalScore;
        if (scoreBreakdown != null) this.scoreBreakdown = scoreBreakdown;
        this.status = status;
        this.message = message;
    }

    public static AssignmentResultDtoBuilder builder() { return new AssignmentResultDtoBuilder(); }

    public static class AssignmentResultDtoBuilder {
        private Long ticketId;
        private Long agentId;
        private String agentName;
        private String teamName;
        private Double finalScore;
        private Map<String, Double> scoreBreakdown = new HashMap<>();
        private String status;
        private String message;

        public AssignmentResultDtoBuilder ticketId(Long ticketId) { this.ticketId = ticketId; return this; }
        public AssignmentResultDtoBuilder agentId(Long agentId) { this.agentId = agentId; return this; }
        public AssignmentResultDtoBuilder agentName(String agentName) { this.agentName = agentName; return this; }
        public AssignmentResultDtoBuilder teamName(String teamName) { this.teamName = teamName; return this; }
        public AssignmentResultDtoBuilder finalScore(Double finalScore) { this.finalScore = finalScore; return this; }
        public AssignmentResultDtoBuilder scoreBreakdown(Map<String, Double> scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; return this; }
        public AssignmentResultDtoBuilder status(String status) { this.status = status; return this; }
        public AssignmentResultDtoBuilder message(String message) { this.message = message; return this; }

        public AssignmentResultDto build() {
            return new AssignmentResultDto(ticketId, agentId, agentName, teamName, finalScore, scoreBreakdown, status, message);
        }
    }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }
    public Map<String, Double> getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(Map<String, Double> scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
