package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.AuditLog;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.AuditAction;
import be.profacile.savefunds.domain.enums.AuditOutcome;

import java.util.List;

public interface AuditLogService {

    AuditLog record(
            User user,
            Long entrepriseId,
            AuditAction action,
            AuditOutcome outcome,
            String resourceType,
            Long resourceId,
            String details
    );

    List<AuditLog> findLastForEntreprise(Long entrepriseId);
}
