package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.AuditAction;
import be.profacile.savefunds.domain.enums.AuditOutcome;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private Long entrepriseId;
    private Long userId;
    private String userEmail;
    private AuditAction action;
    private AuditOutcome outcome;
    private String resourceType;
    private Long resourceId;
    private String details;
    private LocalDateTime createdAt;
}
