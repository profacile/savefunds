package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.response.BnbAnnualAccountsLookupResponse;
import be.profacile.savefunds.api.dto.response.FinancialSnapshotResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.BnbAnnualAccountsLookupMapper;
import be.profacile.savefunds.api.mapper.FinancialSnapshotApiMapper;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.AuditAction;
import be.profacile.savefunds.domain.enums.AuditOutcome;
import be.profacile.savefunds.domain.service.AuditLogService;
import be.profacile.savefunds.domain.service.BnbAnnualAccountsService;
import be.profacile.savefunds.domain.service.EntrepriseService;
import be.profacile.savefunds.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/entreprises/{entrepriseId}/bnb/annual-accounts")
@RequiredArgsConstructor
@Tag(name = "BNB / Comptes annuels", description = "Recherche des comptes annuels publics BNB/CBSO par numero d'entreprise")
public class BnbAnnualAccountsController {

    private final BnbAnnualAccountsService bnbAnnualAccountsService;
    private final BnbAnnualAccountsLookupMapper mapper;
    private final FinancialSnapshotApiMapper financialSnapshotApiMapper;
    private final EntrepriseService entrepriseService;
    private final CurrentUserService currentUserService;
    private final AuditLogService auditLogService;

    @PostMapping("/search")
    @Operation(summary = "Lancer une recherche BNB/CBSO pour une entreprise")
    public BnbAnnualAccountsLookupResponse search(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        return mapper.toResponse(bnbAnnualAccountsService.search(entrepriseId));
    }

    @GetMapping("/latest")
    @Operation(summary = "Recuperer la derniere recherche BNB/CBSO")
    public BnbAnnualAccountsLookupResponse latest(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        return mapper.toResponse(bnbAnnualAccountsService.findLatest(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune recherche BNB pour cette entreprise")));
    }

    @PostMapping("/import-latest")
    @Operation(summary = "Creer un snapshot financier depuis le dernier depot BNB reel")
    public FinancialSnapshotResponse importLatest(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = bnbAnnualAccountsService.createSnapshotFromLatestDeposit(entrepriseId);
        auditLogService.record(
                currentUserService.getCurrentUser(),
                entrepriseId,
                AuditAction.FINANCIAL_SNAPSHOT_IMPORTED,
                AuditOutcome.SUCCESS,
                "FINANCIAL_SNAPSHOT",
                snapshot.getId(),
                "Import BNB/CBSO officiel reference=" + snapshot.getSourceReference()
        );
        return financialSnapshotApiMapper.toResponse(snapshot);
    }

    private void assertOwnsEntreprise(Long entrepriseId) {
        Entreprise entreprise = entrepriseService.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable: " + entrepriseId));
        if (!currentUserService.getCurrentUserId().equals(entreprise.getUserId())) {
            throw new AccessDeniedException("Acces refuse a cette entreprise");
        }
    }
}
