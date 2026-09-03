package com.cloudpilot.repository;

import com.cloudpilot.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    List<Ticket> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Ticket> findByStatus(Ticket.Status status);

    List<Ticket> findByStatusAndPriority(Ticket.Status status, Ticket.Priority priority);

    List<Ticket> findByAssignedAgentId(Long agentId);

    @Query("SELECT t FROM Ticket t WHERE t.status NOT IN ('RESOLVED', 'CLOSED') AND t.slaDeadline IS NOT NULL AND t.slaDeadline < :threshold")
    List<Ticket> findOpenTicketsPastOrNearingDeadline(@Param("threshold") ZonedDateTime threshold);

    @Query("SELECT t FROM Ticket t WHERE t.status NOT IN ('RESOLVED', 'CLOSED') AND (t.riskStatus = 'AT_RISK' OR t.riskStatus = 'BREACHED')")
    List<Ticket> findAtRiskOrBreachedTickets();

    @Query("SELECT t FROM Ticket t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate ORDER BY t.createdAt ASC")
    List<Ticket> findTicketsBetweenTimestampsSorted(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    long countByStatus(Ticket.Status status);

    long countByRiskStatus(Ticket.RiskStatus riskStatus);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = 'RESOLVED' AND t.riskStatus != 'BREACHED'")
    long countResolvedWithinSla();
}
