package com.cloudpilot.service;

import com.cloudpilot.model.AuditLog;
import com.cloudpilot.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog log(String actorId, String actorRole, String action, String entityType, String entityId, String details) {
        AuditLog entry = AuditLog.builder()
                .actorId(actorId != null ? actorId : "anonymous")
                .actorRole(actorRole != null ? actorRole : "SYSTEM")
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();

        AuditLog saved = auditLogRepository.save(entry);
        log.info("[AUDIT] actor={}:{} action={} entity={}:{}", actorId, actorRole, action, entityType, entityId);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getLogs(String entityType, String actorId, ZonedDateTime from, ZonedDateTime to, Pageable pageable) {
        return auditLogRepository.filterAuditLogs(entityType, actorId, from, to, pageable);
    }
}
