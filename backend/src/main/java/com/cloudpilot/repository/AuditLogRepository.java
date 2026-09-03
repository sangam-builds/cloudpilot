package com.cloudpilot.repository;

import com.cloudpilot.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);

    Page<AuditLog> findByActorId(String actorId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE (:entityType IS NULL OR a.entityType = :entityType) " +
           "AND (:actorId IS NULL OR a.actorId = :actorId) " +
           "AND (:from IS NULL OR a.createdAt >= :from) " +
           "AND (:to IS NULL OR a.createdAt <= :to) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLog> filterAuditLogs(@Param("entityType") String entityType,
                                   @Param("actorId") String actorId,
                                   @Param("from") ZonedDateTime from,
                                   @Param("to") ZonedDateTime to,
                                   Pageable pageable);
}
