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

    @Test
    void testLeafNodeFailure_HasZeroDownstreamImpact() {
        // Notification Service is a sink/leaf node with no outgoing edges
        List<String> affected = graph.findAffectedServicesBFS("Notification Service");
        assertTrue(affected.isEmpty(), "Leaf node failure should have 0 downstream affected services");
    }

    @Test
    void testDisconnectedGraphComponent() {
        ServiceDependencyGraph customGraph = new ServiceDependencyGraph();
        customGraph.addEdge("Isolated A", "Isolated B");
        customGraph.addEdge("Cluster X", "Cluster Y");

        List<String> affected = customGraph.findAffectedServicesBFS("Isolated A");
        assertEquals(List.of("Isolated B"), affected);
        assertFalse(affected.contains("Cluster Y"));
    }

    @Test
    void testCycleHandling_DoesNotInfiniteLoop() {
        ServiceDependencyGraph cyclicGraph = new ServiceDependencyGraph();
        cyclicGraph.addEdge("Node 1", "Node 2");
        cyclicGraph.addEdge("Node 2", "Node 3");
        cyclicGraph.addEdge("Node 3", "Node 1"); // Cycle

        List<String> affected = cyclicGraph.findAffectedServicesBFS("Node 1");
        assertEquals(2, affected.size());
        assertTrue(affected.contains("Node 2"));
        assertTrue(affected.contains("Node 3"));
    }
}
