package com.cloudpilot.algorithms;

import com.cloudpilot.model.Ticket;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Binary Search on Chronologically Sorted Incident Logs (DSA Showcase)
 *
 * Provides O(log n) lookup for the first ticket created at or after a given timestamp,
 * enabling sublist slicing for audit, metrics, and incident replay queries.
 */
@Component
public class TicketTimestampSearch {

    /**
     * Binary search to find the index of the first ticket created at or after the target timestamp.
     * Complexity: O(log n) comparisons vs O(n) linear scan.
     *
     * @param sortedTickets List of tickets sorted strictly ascending by createdAt
     * @param targetTime Target cutoff timestamp
     * @return 0-based index of insertion point, or -1 if no tickets meet criteria
     */
    public int findFirstIndexAtOrAfter(List<Ticket> sortedTickets, ZonedDateTime targetTime) {
        if (sortedTickets == null || sortedTickets.isEmpty() || targetTime == null) {
            return -1;
        }

        int low = 0;
        int high = sortedTickets.size() - 1;
        int resultIndex = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Ticket midTicket = sortedTickets.get(mid);
            ZonedDateTime midTime = midTicket.getCreatedAt() != null ? midTicket.getCreatedAt() : ZonedDateTime.now();

            if (!midTime.isBefore(targetTime)) {
                // midTime is >= targetTime; record candidate and search left half
                resultIndex = mid;
                high = mid - 1;
            } else {
                // midTime is < targetTime; search right half
                low = mid + 1;
            }
        }

        return resultIndex;
    }

    /**
     * Slices the sorted ticket list returning all tickets created at or after the target timestamp.
     * Time Complexity: O(log n) to locate boundary + O(k) for sublist view (where k is result count).
     */
    public List<Ticket> findTicketsAfter(List<Ticket> sortedTickets, ZonedDateTime targetTime) {
        int index = findFirstIndexAtOrAfter(sortedTickets, targetTime);
        if (index == -1) {
            return Collections.emptyList();
        }
        return sortedTickets.subList(index, sortedTickets.size());
    }
}
