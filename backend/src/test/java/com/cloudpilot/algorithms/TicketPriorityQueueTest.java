package com.cloudpilot.algorithms;

import com.cloudpilot.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketPriorityQueueTest {

    private TicketPriorityQueue priorityQueue;

    @BeforeEach
    void setUp() {
        priorityQueue = new TicketPriorityQueue();
    }

    @Test
    void testPriorityOrdering_HighPrecedesMediumAndLow() {
        ZonedDateTime baseTime = ZonedDateTime.now();

        Ticket low = Ticket.builder().id(1L).priority(Ticket.Priority.LOW).createdAt(baseTime.minusMinutes(30)).build();
        Ticket medium = Ticket.builder().id(2L).priority(Ticket.Priority.MEDIUM).createdAt(baseTime.minusMinutes(20)).build();
        Ticket high = Ticket.builder().id(3L).priority(Ticket.Priority.HIGH).createdAt(baseTime.minusMinutes(10)).build();

        priorityQueue.push(low);
        priorityQueue.push(medium);
        priorityQueue.push(high);

        assertEquals(3, priorityQueue.size());
        assertEquals(3L, priorityQueue.pollNext().getId()); // HIGH first
        assertEquals(2L, priorityQueue.pollNext().getId()); // MEDIUM second
        assertEquals(1L, priorityQueue.pollNext().getId()); // LOW third
        assertTrue(priorityQueue.isEmpty());
    }

    @Test
    void testFifoTieBreaking_IdenticalPriorityOrdersByCreationTimestamp() {
        ZonedDateTime now = ZonedDateTime.now();

        // Two tickets with identical HIGH priority but different arrival times
        Ticket olderHigh = Ticket.builder().id(10L).priority(Ticket.Priority.HIGH).createdAt(now.minusMinutes(15)).build();
        Ticket newerHigh = Ticket.builder().id(11L).priority(Ticket.Priority.HIGH).createdAt(now.minusMinutes(5)).build();

        // Insert newer one first
        priorityQueue.push(newerHigh);
        priorityQueue.push(olderHigh);

        // Older must be polled first (strict FIFO tie-breaking)
        Ticket firstOut = priorityQueue.pollNext();
        assertNotNull(firstOut);
        assertEquals(10L, firstOut.getId(), "Older ticket must be served first under identical priority");

        Ticket secondOut = priorityQueue.pollNext();
        assertNotNull(secondOut);
        assertEquals(11L, secondOut.getId());
    }

    @Test
    void testPeekAndClear() {
        Ticket ticket = Ticket.builder().id(100L).priority(Ticket.Priority.MEDIUM).build();
        priorityQueue.push(ticket);

        assertEquals(100L, priorityQueue.peek().getId());
        assertEquals(1, priorityQueue.size());

        priorityQueue.clear();
        assertTrue(priorityQueue.isEmpty());
        assertNull(priorityQueue.pollNext());
    }

    @Test
    void testToList_ReturnsSortedSnapshot() {
        ZonedDateTime now = ZonedDateTime.now();
        Ticket t1 = Ticket.builder().id(1L).priority(Ticket.Priority.LOW).createdAt(now).build();
        Ticket t2 = Ticket.builder().id(2L).priority(Ticket.Priority.HIGH).createdAt(now).build();

        priorityQueue.push(t1);
        priorityQueue.push(t2);

        List<Ticket> list = priorityQueue.toList();
        assertEquals(2, list.size());
        assertEquals(2L, list.get(0).getId()); // HIGH
        assertEquals(1L, list.get(1).getId()); // LOW
    }
}
