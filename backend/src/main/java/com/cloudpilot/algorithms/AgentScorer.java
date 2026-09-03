package com.cloudpilot.algorithms;

import com.cloudpilot.model.Agent;
import com.cloudpilot.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Weighted Agent Scoring Algorithm (DSA showcase)
 *
 * Formula:
 * Score = (W_SKILL * skillMatch) + (W_WORKLOAD * (1 / (1 + currentWorkload)))
 *       + (W_RATING * (rating / 5.0)) + (W_AVAILABILITY * availabilityBonus)
 *
 * Time Complexity for rankAgents: O(n log n) where n is the number of candidate agents.
 */
@Component
public class AgentScorer {

    public static final double WEIGHT_SKILL = 0.40;
    public static final double WEIGHT_WORKLOAD = 0.30;
    public static final double WEIGHT_RATING = 0.20;
    public static final double WEIGHT_AVAILABILITY = 0.10;

    public record AgentScoreResult(
            Agent agent,
            double totalScore,
            Map<String, Double> breakdown
    ) {}

    /**
     * Computes the weighted score for an individual agent given a ticket.
     */
    public AgentScoreResult score(Agent agent, Ticket ticket) {
        if (agent == null) {
            return new AgentScoreResult(null, 0.0, Collections.emptyMap());
        }

        // 1. Skill Match Component (0.0 to 1.0)
        double skillMatchScore = computeSkillMatch(agent, ticket);

        // 2. Workload Component (1 / (1 + workload)) - inversely proportional to active load
        int workload = agent.getCurrentWorkload() != null ? agent.getCurrentWorkload() : 0;
        double workloadScore = 1.0 / (1.0 + Math.max(0, workload));

        // 3. Rating Component (normalized rating out of 5.0)
        double ratingVal = agent.getRating() != null ? agent.getRating().doubleValue() : 5.0;
        double ratingScore = Math.min(1.0, Math.max(0.0, ratingVal / 5.0));

        // 4. Availability Bonus
        boolean isAvailable = Boolean.TRUE.equals(agent.getIsAvailable());
        double availabilityScore = isAvailable ? 1.0 : 0.0;

        // Weighted aggregation
        double weightedSkill = WEIGHT_SKILL * skillMatchScore;
        double weightedWorkload = WEIGHT_WORKLOAD * workloadScore;
        double weightedRating = WEIGHT_RATING * ratingScore;
        double weightedAvailability = WEIGHT_AVAILABILITY * availabilityScore;

        double totalScore = weightedSkill + weightedWorkload + weightedRating + weightedAvailability;

        Map<String, Double> breakdown = new LinkedHashMap<>();
        breakdown.put("skillMatch", Math.round(weightedSkill * 1000.0) / 1000.0);
        breakdown.put("workloadEfficiency", Math.round(weightedWorkload * 1000.0) / 1000.0);
        breakdown.put("performanceRating", Math.round(weightedRating * 1000.0) / 1000.0);
        breakdown.put("availabilityBonus", Math.round(weightedAvailability * 1000.0) / 1000.0);
        breakdown.put("rawSkillOverlap", skillMatchScore);
        breakdown.put("totalScore", Math.round(totalScore * 1000.0) / 1000.0);

        return new AgentScoreResult(agent, totalScore, breakdown);
    }

    /**
     * Ranks candidates in descending order of score.
     * Complexity: O(n log n)
     */
    public List<AgentScoreResult> rankAgents(List<Agent> agents, Ticket ticket) {
        if (agents == null || agents.isEmpty()) {
            return Collections.emptyList();
        }

        List<AgentScoreResult> results = new ArrayList<>();
        for (Agent agent : agents) {
            results.add(score(agent, ticket));
        }

        results.sort((a, b) -> Double.compare(b.totalScore(), a.totalScore()));
        return results;
    }

    private double computeSkillMatch(Agent agent, Ticket ticket) {
        List<String> agentSkills = agent.getSkillTagsList();
        if (agentSkills.isEmpty()) {
            return 0.5; // baseline neutral score if no specific tags
        }

        String searchContext = ((ticket.getCategory() != null ? ticket.getCategory() : "") + " " +
                                (ticket.getSubject() != null ? ticket.getSubject() : "") + " " +
                                (ticket.getDescription() != null ? ticket.getDescription() : "")).toLowerCase();

        long matchedSkills = agentSkills.stream()
                .filter(skill -> searchContext.contains(skill.toLowerCase()))
                .count();

        if (agentSkills.isEmpty()) return 0.5;
        double ratio = (double) matchedSkills / agentSkills.size();
        return Math.min(1.0, 0.3 + (ratio * 0.7)); // Floor of 0.3 for team members + overlap boost
    }
}
