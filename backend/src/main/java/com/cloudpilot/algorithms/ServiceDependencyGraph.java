package com.cloudpilot.algorithms;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Service Dependency Graph (DSA Showcase)
 * Models microservice topologies and calculates outage blast radius via BFS and DFS traversals.
 *
 * Example Graph:
 * Payment Gateway -> Order Service -> Fulfillment/Shipping -> Delivery Notifications
 * Auth Service -> All Services
 * Database/Storage -> Order Service, Customer 360
 */
@Component
public class ServiceDependencyGraph {

    private final Map<String, List<String>> adjacencyList = new HashMap<>();

    public ServiceDependencyGraph() {
        initializeDefaultGraph();
    }

    private void initializeDefaultGraph() {
        // Directed dependency edges: A -> B means B depends on A (if A fails, B is impacted)
        addEdge("Auth Service", "API Gateway");
        addEdge("Database Primary", "User Service");
        addEdge("Database Primary", "Order Service");
        addEdge("Database Primary", "Payment Service");
        addEdge("Payment Service", "Order Service");
        addEdge("Order Service", "Fulfillment Service");
        addEdge("Order Service", "Invoice Service");
        addEdge("Fulfillment Service", "Shipping Logistics");
        addEdge("Shipping Logistics", "Notification Service");
        addEdge("AI Classification Worker", "Ticket Triage Service");
        addEdge("Redis Cache", "Customer 360 Service");
        addEdge("Redis Cache", "Session Store");
    }

    public synchronized void addEdge(String source, String destination) {
        adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(destination);
        adjacencyList.putIfAbsent(destination, new ArrayList<>());
    }

    /**
     * Breadth-First Search (BFS) to find all downstream impacted services layer by layer.
     * Complexity: O(V + E) where V is services and E is dependency links.
     */
    public List<String> findAffectedServicesBFS(String failedService) {
        if (!adjacencyList.containsKey(failedService)) {
            return Collections.emptyList();
        }

        List<String> affected = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(failedService);
        visited.add(failedService);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (!current.equals(failedService)) {
                affected.add(current);
            }

            for (String neighbor : adjacencyList.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return affected;
    }

    /**
     * Depth-First Search (DFS) traversal to detect dependency chains and cycle presence.
     * Complexity: O(V + E)
     */
    public List<String> findAffectedServicesDFS(String failedService) {
        List<String> affected = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfsHelper(failedService, failedService, visited, affected);
        return affected;
    }

    private void dfsHelper(String root, String current, Set<String> visited, List<String> affected) {
        visited.add(current);
        if (!current.equals(root)) {
            affected.add(current);
        }

        for (String neighbor : adjacencyList.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                dfsHelper(root, neighbor, visited, affected);
            }
        }
    }

    public Map<String, List<String>> getAdjacencyList() {
        return Collections.unmodifiableMap(adjacencyList);
    }
}
