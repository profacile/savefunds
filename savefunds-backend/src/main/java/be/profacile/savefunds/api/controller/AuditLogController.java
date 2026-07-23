package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.response.AuditLogResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.AuditLogApiMapper;
import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.service.AuditLogService;
import be.profacile.savefunds.domain.service.CompanyService;
import be.profacile.savefunds.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies/{companyId}/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Tracabilite des actions sensibles par company")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final CompanyService companyService;
    private final CurrentUserService currentUserService;
    private final AuditLogApiMapper auditLogMapper;

    @GetMapping
    @Operation(summary = "Consulter les 50 dernieres actions auditees d'une company")
    public ResponseEntity<List<AuditLogResponse>> findLastAuditLogs(@PathVariable Long companyId) {
        assertOwnsCompany(companyId);

        List<AuditLogResponse> responses = auditLogService.findLastForCompany(companyId)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    private void assertOwnsCompany(Long companyId) {
        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company introuvable: " + companyId));
        if (!currentUserService.getCurrentUserId().equals(company.getUserId())) {
            throw new AccessDeniedException("Acces refuse a cette company");
        }
    }
}
