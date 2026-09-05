package com.cloudpilot.algorithms;

import com.cloudpilot.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketTimestampSearchTest {

    private TicketTimestampSearch searchEngine;

    @BeforeEach
    void setUp() {
        searchEngine = new TicketTimestampSearch();
    }

    @Test
    void testFindFirstIndexAtOrAfter_ExactAndBetweenMatches() {
        ZonedDateTime base = ZonedDateTime.parse("2026-09-01T10:00:00Z");

        Ticket t0 = Ticket.builder().id(0L).createdAt(base).build();
        Ticket t1 = Ticket.builder().id(1L).createdAt(base.plusMinutes(15)).build();
        Ticket t2 = Ticket.builder().id(2L).createdAt(base.plusMinutes(30)).build();
        Ticket t3 = Ticket.builder().id(3L).createdAt(base.plusMinutes(45)).build();
        Ticket t4 = Ticket.builder().id(4L).createdAt(base.plusMinutes(60)).build();

        List<Ticket> sortedTickets = List.of(t0, t1, t2, t3, t4);

        // Exact match
        assertEquals(2, searchEngine.findFirstIndexAtOrAfter(sortedTickets, base.plusMinutes(30)));

        // Between timestamps (e.g. 10:20 -> should return index 2 for 10:30)
        assertEquals(2, searchEngine.findFirstIndexAtOrAfter(sortedTickets, base.plusMinutes(20)));

        // Target before all elements -> index 0
        assertEquals(0, searchEngine.findFirstIndexAtOrAfter(sortedTickets, base.minusMinutes(10)));

        // Target after all elements -> -1
        assertEquals(-1, searchEngine.findFirstIndexAtOrAfter(sortedTickets, base.plusMinutes(90)));
    }

    @Test
    void testFindTicketsAfter_ReturnsSublist() {
        ZonedDateTime base = ZonedDateTime.parse("2026-09-01T10:00:00Z");
        Ticket t0 = Ticket.builder().id(0L).createdAt(base).build();
        Ticket t1 = Ticket.builder().id(1L).createdAt(base.plusMinutes(15)).build();
        Ticket t2 = Ticket.builder().id(2L).createdAt(base.plusMinutes(30)).build();

        List<Ticket> result = searchEngine.findTicketsAfter(List.of(t0, t1, t2), base.plusMinutes(15));
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testEmptyAndNullEdgeCases() {
        assertEquals(-1, searchEngine.findFirstIndexAtOrAfter(Collections.emptyList(), ZonedDateTime.now()));
        assertEquals(-1, searchEngine.findFirstIndexAtOrAfter(null, ZonedDateTime.now()));
        assertEquals(-1, searchEngine.findFirstIndexAtOrAfter(List.of(Ticket.builder().build()), null));
        assertTrue(searchEngine.findTicketsAfter(Collections.emptyList(), ZonedDateTime.now()).isEmpty());
    }
}
