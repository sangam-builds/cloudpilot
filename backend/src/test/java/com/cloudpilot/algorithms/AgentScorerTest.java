package com.cloudpilot.algorithms;

import com.cloudpilot.model.Agent;
import com.cloudpilot.model.Team;
import com.cloudpilot.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentScorerTest {

    private AgentScorer agentScorer;

    @BeforeEach
    void setUp() {
        agentScorer = new AgentScorer();
    }

    @Test
    void testScore_HighSkillMatchAndLowWorkload_Wins() {
        Team paymentsTeam = Team.builder().id(1L).name("Payments").build();

        Agent specialist = Agent.builder()
                .id(1L)
                .name("Alex Mercer")
                .team(paymentsTeam)
                .skillTags("refunds,stripe,chargebacks")
                .rating(BigDecimal.valueOf(4.95))
                .isAvailable(true)
                .currentWorkload(1)
                .build();

        Agent generalist = Agent.builder()
                .id(2L)
                .name("Sarah Connor")
                .team(paymentsTeam)
                .skillTags("general,tax")
                .rating(BigDecimal.valueOf(4.50))
                .isAvailable(true)
                .currentWorkload(5)
                .build();

        Ticket ticket = Ticket.builder()
                .id(101L)
                .subject("Stripe chargeback refund issue")
                .description("Customer requested immediate refund on duplicate stripe charge.")
                .category("Payments")
                .priority(Ticket.Priority.HIGH)
                .build();

        AgentScorer.AgentScoreResult specialistResult = agentScorer.score(specialist, ticket);
        AgentScorer.AgentScoreResult generalistResult = agentScorer.score(generalist, ticket);

        assertTrue(specialistResult.totalScore() > generalistResult.totalScore(), "Specialist with low workload should outscore busy generalist");
        assertTrue(specialistResult.breakdown().containsKey("skillMatch"));
        assertTrue(specialistResult.breakdown().containsKey("workloadEfficiency"));
    }

    @Test
    void testRankAgents_SortsDescending() {
        Team techTeam = Team.builder().id(2L).name("Technical Support").build();

        Agent a1 = Agent.builder().id(1L).name("David").team(techTeam).skillTags("api,database").rating(BigDecimal.valueOf(4.9)).isAvailable(true).currentWorkload(0).build();
        Agent a2 = Agent.builder().id(2L).name("Elena").team(techTeam).skillTags("ui").rating(BigDecimal.valueOf(4.2)).isAvailable(true).currentWorkload(4).build();
        Agent a3 = Agent.builder().id(3L).name("Busy").team(techTeam).skillTags("api").rating(BigDecimal.valueOf(4.0)).isAvailable(false).currentWorkload(8).build();

        Ticket ticket = Ticket.builder().subject("API rate limit error").description("Database latency and api 429").category("Technical Support").build();

        List<AgentScorer.AgentScoreResult> ranked = agentScorer.rankAgents(List.of(a2, a3, a1), ticket);

        assertEquals(3, ranked.size());
        assertEquals("David", ranked.get(0).agent().getName());
        assertTrue(ranked.get(0).totalScore() >= ranked.get(1).totalScore());
        assertTrue(ranked.get(1).totalScore() >= ranked.get(2).totalScore());
    }
}
