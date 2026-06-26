package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.AuditLogResponse;
import be.profacile.savefunds.domain.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogApiMapper {

    public AuditLogResponse toResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .entrepriseId(auditLog.getEntreprise() != null ? auditLog.getEntreprise().getId() : null)
                .userId(auditLog.getUserId())
                .userEmail(auditLog.getUserEmail())
                .action(auditLog.getAction())
                .outcome(auditLog.getOutcome())
                .resourceType(auditLog.getResourceType())
                .resourceId(auditLog.getResourceId())
                .details(auditLog.getDetails())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
