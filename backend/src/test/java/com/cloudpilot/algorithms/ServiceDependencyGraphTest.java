package com.cloudpilot.algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceDependencyGraphTest {

    private ServiceDependencyGraph graph;

    @BeforeEach
    void setUp() {
        graph = new ServiceDependencyGraph();
    }

    @Test
    void testFindAffectedServicesBFS_PaymentOutage() {
        List<String> affected = graph.findAffectedServicesBFS("Payment Service");

        assertNotNull(affected);
        assertTrue(affected.contains("Order Service"));
        assertTrue(affected.contains("Fulfillment Service"));
        assertTrue(affected.contains("Shipping Logistics"));
        assertTrue(affected.contains("Notification Service"));
        assertFalse(affected.contains("Payment Service"), "Should not include the root source node");
    }

    @Test
    void testFindAffectedServicesDFS_MatchBFSReachableNodes() {
        List<String> bfs = graph.findAffectedServicesBFS("Payment Service");
        List<String> dfs = graph.findAffectedServicesDFS("Payment Service");

        assertEquals(bfs.size(), dfs.size());
        assertTrue(dfs.containsAll(bfs));
    }

    @Test
    void testUnknownService_ReturnsEmpty() {
        List<String> affected = graph.findAffectedServicesBFS("NonExistentService");
        assertTrue(affected.isEmpty());
    }
}
