package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.response.AuditLogResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.AuditLogApiMapper;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.AuditAction;
import be.profacile.savefunds.domain.enums.AuditOutcome;
import be.profacile.savefunds.domain.service.AuditLogService;
import be.profacile.savefunds.domain.service.EntrepriseService;
import be.profacile.savefunds.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entreprises/{entrepriseId}/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Tracabilite des actions sensibles par entreprise")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final EntrepriseService entrepriseService;
    private final CurrentUserService currentUserService;
    private final AuditLogApiMapper auditLogMapper;

    @GetMapping
    @Operation(summary = "Consulter les 50 dernieres actions auditees d'une entreprise")
    public ResponseEntity<List<AuditLogResponse>> findLastAuditLogs(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        User user = currentUserService.getCurrentUser();

        auditLogService.record(
                user,
                entrepriseId,
                AuditAction.AUDIT_LOG_VIEWED,
                AuditOutcome.SUCCESS,
                "AUDIT_LOG",
                null,
                "Consultation des journaux d'audit"
        );

        List<AuditLogResponse> responses = auditLogService.findLastForEntreprise(entrepriseId)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    private void assertOwnsEntreprise(Long entrepriseId) {
        Entreprise entreprise = entrepriseService.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable: " + entrepriseId));
        if (!currentUserService.getCurrentUserId().equals(entreprise.getUserId())) {
            throw new AccessDeniedException("Acces refuse a cette entreprise");
        }
    }
}
