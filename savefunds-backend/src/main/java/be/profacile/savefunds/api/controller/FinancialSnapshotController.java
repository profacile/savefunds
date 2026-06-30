package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateManualFinancialSnapshotRequest;
import be.profacile.savefunds.api.dto.request.SimulateFinancialDecisionRequest;
import be.profacile.savefunds.api.dto.response.BankTransactionResponse;
import be.profacile.savefunds.api.dto.response.FinancialSnapshotResponse;
import be.profacile.savefunds.api.dto.response.VigilanceResultResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.BankTransactionApiMapper;
import be.profacile.savefunds.api.mapper.FinancialSnapshotApiMapper;
import be.profacile.savefunds.domain.entity.BankTransaction;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.AuditAction;
import be.profacile.savefunds.domain.enums.AuditOutcome;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import be.profacile.savefunds.domain.service.AuditLogService;
import be.profacile.savefunds.domain.service.BankTransactionService;
import be.profacile.savefunds.domain.service.EntrepriseService;
import be.profacile.savefunds.domain.service.FinancialSnapshotService;
import be.profacile.savefunds.domain.service.VigilanceEngine;
import be.profacile.savefunds.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entreprises/{entrepriseId}/financial-snapshots")
@RequiredArgsConstructor
@Tag(name = "Financial snapshots", description = "Ingestion et normalisation de donnees financieres")
public class FinancialSnapshotController {

    private final EntrepriseService entrepriseService;
    private final FinancialSnapshotService snapshotService;
    private final VigilanceEngine vigilanceEngine;
    private final CurrentUserService currentUserService;
    private final FinancialSnapshotApiMapper snapshotMapper;
    private final BankTransactionApiMapper bankTransactionApiMapper;
    private final AuditLogService auditLogService;
    private final BankTransactionService bankTransactionService;

    @PostMapping("/manual")
    @Operation(summary = "Creer un snapshot financier manuel")
    public ResponseEntity<FinancialSnapshotResponse> createManualSnapshot(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody CreateManualFinancialSnapshotRequest request) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.createManualSnapshot(entrepriseId, request);
        auditLogService.record(
                currentUserService.getCurrentUser(),
                entrepriseId,
                AuditAction.FINANCIAL_SNAPSHOT_CREATED,
                AuditOutcome.SUCCESS,
                "FINANCIAL_SNAPSHOT",
                snapshot.getId(),
                "Snapshot financier manuel cree"
        );
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @PostMapping(value = "/import-bank-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importer un extrait bancaire CSV normalise")
    public ResponseEntity<FinancialSnapshotResponse> importBankCsv(
            @PathVariable Long entrepriseId,
            @RequestPart("file") MultipartFile file) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.importSnapshot(
                entrepriseId,
                file,
                FinancialSnapshotSource.BANK_CSV,
                currentUserService.getCurrentUserId()
        );
        List<BankTransaction> transactions = bankTransactionService.importAndClassify(entrepriseId, snapshot, file);
        auditLogService.record(
                currentUserService.getCurrentUser(),
                entrepriseId,
                AuditAction.FINANCIAL_SNAPSHOT_IMPORTED,
                AuditOutcome.SUCCESS,
                "FINANCIAL_SNAPSHOT",
                snapshot.getId(),
                "Import bancaire CSV: " + file.getOriginalFilename() + " transactions=" + transactions.size()
        );
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @GetMapping("/bank-transactions")
    @Operation(summary = "Lister les transactions bancaires classees")
    public ResponseEntity<List<BankTransactionResponse>> bankTransactions(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        List<BankTransactionResponse> transactions = bankTransactionService.findByEntreprise(entrepriseId).stream()
                .map(bankTransactionApiMapper::toResponse)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @PostMapping(value = "/import-accounting-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importer un export comptable CSV normalise")
    public ResponseEntity<FinancialSnapshotResponse> importAccountingCsv(
            @PathVariable Long entrepriseId,
            @RequestPart("file") MultipartFile file) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.importSnapshot(
                entrepriseId,
                file,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                currentUserService.getCurrentUserId()
        );
        auditLogService.record(
                currentUserService.getCurrentUser(),
                entrepriseId,
                AuditAction.FINANCIAL_SNAPSHOT_IMPORTED,
                AuditOutcome.SUCCESS,
                "FINANCIAL_SNAPSHOT",
                snapshot.getId(),
                "Import comptable CSV: " + file.getOriginalFilename()
        );
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @PostMapping("/mock-bank")
    @Operation(summary = "Creer un snapshot depuis une simulation de connexion bancaire PSD2")
    public ResponseEntity<FinancialSnapshotResponse> createMockBankSnapshot(@PathVariable Long entrepriseId) {
        return createMockExternalSnapshot(
                entrepriseId,
                FinancialSnapshotSource.BANK_API,
                "Simulation connecteur bancaire PSD2"
        );
    }

    @PostMapping("/mock-balance-sheet")
    @Operation(summary = "Creer un snapshot depuis une simulation de parsing de bilan")
    public ResponseEntity<FinancialSnapshotResponse> createMockBalanceSheetSnapshot(@PathVariable Long entrepriseId) {
        return createMockExternalSnapshot(
                entrepriseId,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                "Simulation parser de bilan"
        );
    }

    @GetMapping("/latest")
    @Operation(summary = "Recuperer le dernier snapshot financier")
    public ResponseEntity<FinancialSnapshotResponse> latestSnapshot(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.findLatest(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun snapshot financier pour cette entreprise"));
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @GetMapping
    @Operation(summary = "Lister les snapshots financiers d'une entreprise")
    public ResponseEntity<List<FinancialSnapshotResponse>> snapshots(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        List<FinancialSnapshotResponse> snapshots = snapshotService.findAll(entrepriseId).stream()
                .map(snapshotMapper::toResponse)
                .toList();
        return ResponseEntity.ok(snapshots);
    }

    @GetMapping("/consolidated")
    @Operation(summary = "Construire la situation financiere consolidee selon la hierarchie SaveFunds")
    public ResponseEntity<FinancialSnapshotResponse> consolidatedSnapshot(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.buildConsolidatedSnapshot(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune source financiere disponible pour cette entreprise"));
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @PostMapping("/simulate")
    @Operation(summary = "Simuler une decision financiere sur le dernier snapshot")
    public ResponseEntity<VigilanceResultResponse> simulateDecision(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody SimulateFinancialDecisionRequest request) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = request.getForcedSource() == null
                ? snapshotService.buildConsolidatedSnapshot(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune source financiere disponible pour simuler une decision"))
                : snapshotService.findLatestBySource(entrepriseId, request.getForcedSource())
                .orElseThrow(() -> new ResourceNotFoundException("Aucune source " + request.getForcedSource() + " disponible pour cette entreprise"));
        VigilanceResultResponse result = vigilanceEngine.simulate(snapshot, request);
        auditLogService.record(
                currentUserService.getCurrentUser(),
                entrepriseId,
                AuditAction.FINANCIAL_DECISION_SIMULATED,
                AuditOutcome.SUCCESS,
                "FINANCIAL_SNAPSHOT",
                snapshot.getId(),
                "Simulation " + request.getType() + " montant=" + request.getAmount()
                        + " source=" + (request.getForcedSource() == null ? "AUTO_HIERARCHY" : request.getForcedSource())
                        + " decision=" + result.getGlobalDecision()
        );
        return ResponseEntity.ok(result);
    }

    private void assertOwnsEntreprise(Long entrepriseId) {
        Entreprise entreprise = entrepriseService.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable: " + entrepriseId));
        if (!currentUserService.getCurrentUserId().equals(entreprise.getUserId())) {
            throw new AccessDeniedException("Acces refuse a cette entreprise");
        }
    }

    private ResponseEntity<FinancialSnapshotResponse> createMockExternalSnapshot(
            Long entrepriseId,
            FinancialSnapshotSource source,
            String auditDetails
    ) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.createExternalSnapshot(
                entrepriseId,
                source,
                currentUserService.getCurrentUserId()
        );
        auditLogService.record(
                currentUserService.getCurrentUser(),
                entrepriseId,
                AuditAction.FINANCIAL_SNAPSHOT_IMPORTED,
                AuditOutcome.SUCCESS,
                "FINANCIAL_SNAPSHOT",
                snapshot.getId(),
                auditDetails + " source=" + source
        );
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }
}
