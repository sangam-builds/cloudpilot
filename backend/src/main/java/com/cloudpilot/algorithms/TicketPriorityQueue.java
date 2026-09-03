package com.cloudpilot.algorithms;

import com.cloudpilot.model.Ticket;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Custom in-memory Priority Queue data structure for scheduling unassigned tickets.
 * Ordering:
 * 1. Priority Weight: HIGH (rank 1) > MEDIUM (rank 2) > LOW (rank 3)
 * 2. Secondary Order: FIFO by creation timestamp (earlier tickets processed first)
 */
@Component
public class TicketPriorityQueue {

    private final PriorityQueue<Ticket> queue;

    public TicketPriorityQueue() {
        this.queue = new PriorityQueue<>(TICKET_COMPARATOR);
    }

    public static final Comparator<Ticket> TICKET_COMPARATOR = (t1, t2) -> {
        int rank1 = getPriorityRank(t1.getPriority());
        int rank2 = getPriorityRank(t2.getPriority());

        if (rank1 != rank2) {
            return Integer.compare(rank1, rank2); // Lower rank value = Higher priority
        }

        ZonedDateTime time1 = t1.getCreatedAt() != null ? t1.getCreatedAt() : ZonedDateTime.now();
        ZonedDateTime time2 = t2.getCreatedAt() != null ? t2.getCreatedAt() : ZonedDateTime.now();
        return time1.compareTo(time2); // Ascending: older first
    };

    private static int getPriorityRank(Ticket.Priority priority) {
        if (priority == null) return 3;
        return switch (priority) {
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }

    public synchronized void push(Ticket ticket) {
        if (ticket != null) {
            queue.offer(ticket);
        }
    }

    public synchronized Ticket pollNext() {
        return queue.poll();
    }

    public synchronized Ticket peek() {
        return queue.peek();
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized List<Ticket> toList() {
        List<Ticket> list = new ArrayList<>(queue);
        list.sort(TICKET_COMPARATOR);
        return Collections.unmodifiableList(list);
    }

    public synchronized void clear() {
        queue.clear();
    }
}
