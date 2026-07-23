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
import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.AuditAction;
import be.profacile.savefunds.domain.enums.AuditOutcome;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import be.profacile.savefunds.domain.service.AuditLogService;
import be.profacile.savefunds.domain.service.BankTransactionService;
import be.profacile.savefunds.domain.service.CompanyService;
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
@RequestMapping("/api/v1/companies/{companyId}/financial-snapshots")
@RequiredArgsConstructor
@Tag(name = "Financial snapshots", description = "Ingestion et normalisation de donnees financieres")
public class FinancialSnapshotController {

    private final CompanyService companyService;
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
            @PathVariable Long companyId,
            @Valid @RequestBody CreateManualFinancialSnapshotRequest request) {
        assertOwnsCompany(companyId);
        FinancialSnapshot snapshot = snapshotService.createManualSnapshot(companyId, request);
        auditLogService.record(
                currentUserService.getCurrentUser(),
                companyId,
                AuditAction.FINANCIAL_SNAPSHOT_CREATED,
                AuditOutcome.SUCCESS,
                "FINANCIAL_SNAPSHOT",
                snapshot.getId(),
                "Snapshot financier manuel cree"
        );
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @PostMapping(value = "/import-bank-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importer un extrait bancaire",
            description = "MVP: parsing CSV normalise. Production: PDF, Excel, CODA/STA ou PSD2.")
    public ResponseEntity<FinancialSnapshotResponse> importBankCsv(
            @PathVariable Long companyId,
            @RequestPart("file") MultipartFile file) {
        assertOwnsCompany(companyId);
        FinancialSnapshot snapshot = snapshotService.importSnapshot(
                companyId,
                file,
                FinancialSnapshotSource.BANK_CSV,
                currentUserService.getCurrentUserId()
        );
        List<BankTransaction> transactions = bankTransactionService.importAndClassify(companyId, snapshot, file);
        auditLogService.record(
                currentUserService.getCurrentUser(),
                companyId,
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
    public ResponseEntity<List<BankTransactionResponse>> bankTransactions(@PathVariable Long companyId) {
        assertOwnsCompany(companyId);
        List<BankTransactionResponse> transactions = bankTransactionService.findByCompany(companyId).stream()
                .map(bankTransactionApiMapper::toResponse)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @PostMapping(value = "/import-accounting-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importer un bilan provisoire",
            description = "MVP: parsing CSV comptable normalise. Production: PDF, Word, Excel ou image via extraction IA/OCR.")
    public ResponseEntity<FinancialSnapshotResponse> importAccountingCsv(
            @PathVariable Long companyId,
            @RequestPart("file") MultipartFile file) {
        assertOwnsCompany(companyId);
        FinancialSnapshot snapshot = snapshotService.importSnapshot(
                companyId,
                file,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                currentUserService.getCurrentUserId()
        );
        auditLogService.record(
                currentUserService.getCurrentUser(),
                companyId,
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
    public ResponseEntity<FinancialSnapshotResponse> createMockBankSnapshot(@PathVariable Long companyId) {
        return createMockExternalSnapshot(
                companyId,
                FinancialSnapshotSource.BANK_API,
                "Simulation connecteur bancaire PSD2"
        );
    }

    @PostMapping("/mock-balance-sheet")
    @Operation(summary = "Creer un snapshot depuis une simulation de parsing de bilan")
    public ResponseEntity<FinancialSnapshotResponse> createMockBalanceSheetSnapshot(@PathVariable Long companyId) {
        return createMockExternalSnapshot(
                companyId,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                "Simulation parser de bilan"
        );
    }

    @GetMapping("/latest")
    @Operation(summary = "Recuperer le dernier snapshot financier")
    public ResponseEntity<FinancialSnapshotResponse> latestSnapshot(@PathVariable Long companyId) {
        assertOwnsCompany(companyId);
        FinancialSnapshot snapshot = snapshotService.findLatest(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun snapshot financier pour cette company"));
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @GetMapping
    @Operation(summary = "Lister les snapshots financiers d'une company")
    public ResponseEntity<List<FinancialSnapshotResponse>> snapshots(@PathVariable Long companyId) {
        assertOwnsCompany(companyId);
        List<FinancialSnapshotResponse> snapshots = snapshotService.findAll(companyId).stream()
                .map(snapshotMapper::toResponse)
                .toList();
        return ResponseEntity.ok(snapshots);
    }

    @GetMapping("/consolidated")
    @Operation(summary = "Construire la situation financiere consolidee selon la hierarchie SaveFunds")
    public ResponseEntity<FinancialSnapshotResponse> consolidatedSnapshot(@PathVariable Long companyId) {
        assertOwnsCompany(companyId);
        FinancialSnapshot snapshot = snapshotService.buildConsolidatedSnapshot(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune source financiere disponible pour cette company"));
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @PostMapping("/simulate")
    @Operation(summary = "Simuler une decision financiere sur le dernier snapshot")
    public ResponseEntity<VigilanceResultResponse> simulateDecision(
            @PathVariable Long companyId,
            @Valid @RequestBody SimulateFinancialDecisionRequest request) {
        assertOwnsCompany(companyId);
        FinancialSnapshot snapshot = request.getForcedSource() == null
                ? snapshotService.buildConsolidatedSnapshot(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune source financiere disponible pour simuler une decision"))
                : snapshotService.findLatestBySource(companyId, request.getForcedSource())
                .orElseThrow(() -> new ResourceNotFoundException("Aucune source " + request.getForcedSource() + " disponible pour cette company"));
        VigilanceResultResponse result = vigilanceEngine.simulate(snapshot, request);
        auditLogService.record(
                currentUserService.getCurrentUser(),
                companyId,
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

    private void assertOwnsCompany(Long companyId) {
        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company introuvable: " + companyId));
        if (!currentUserService.getCurrentUserId().equals(company.getUserId())) {
            throw new AccessDeniedException("Acces refuse a cette company");
        }
    }

    private ResponseEntity<FinancialSnapshotResponse> createMockExternalSnapshot(
            Long companyId,
            FinancialSnapshotSource source,
            String auditDetails
    ) {
        assertOwnsCompany(companyId);
        FinancialSnapshot snapshot = snapshotService.createExternalSnapshot(
                companyId,
                source,
                currentUserService.getCurrentUserId()
        );
        auditLogService.record(
                currentUserService.getCurrentUser(),
                companyId,
                AuditAction.FINANCIAL_SNAPSHOT_IMPORTED,
                AuditOutcome.SUCCESS,
                "FINANCIAL_SNAPSHOT",
                snapshot.getId(),
                auditDetails + " source=" + source
        );
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }
}
