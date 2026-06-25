package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateManualFinancialSnapshotRequest;
import be.profacile.savefunds.api.dto.request.SimulateFinancialDecisionRequest;
import be.profacile.savefunds.api.dto.response.FinancialSnapshotResponse;
import be.profacile.savefunds.api.dto.response.VigilanceResultResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.FinancialSnapshotApiMapper;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
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

    @PostMapping("/manual")
    @Operation(summary = "Creer un snapshot financier manuel")
    public ResponseEntity<FinancialSnapshotResponse> createManualSnapshot(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody CreateManualFinancialSnapshotRequest request) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.createManualSnapshot(entrepriseId, request);
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
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
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
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @GetMapping("/latest")
    @Operation(summary = "Recuperer le dernier snapshot financier")
    public ResponseEntity<FinancialSnapshotResponse> latestSnapshot(@PathVariable Long entrepriseId) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.findLatest(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun snapshot financier pour cette entreprise"));
        return ResponseEntity.ok(snapshotMapper.toResponse(snapshot));
    }

    @PostMapping("/simulate")
    @Operation(summary = "Simuler une decision financiere sur le dernier snapshot")
    public ResponseEntity<VigilanceResultResponse> simulateDecision(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody SimulateFinancialDecisionRequest request) {
        assertOwnsEntreprise(entrepriseId);
        FinancialSnapshot snapshot = snapshotService.findLatest(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun snapshot financier disponible pour simuler une decision"));
        return ResponseEntity.ok(vigilanceEngine.simulate(snapshot, request));
    }

    private void assertOwnsEntreprise(Long entrepriseId) {
        Entreprise entreprise = entrepriseService.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable: " + entrepriseId));
        if (!currentUserService.getCurrentUserId().equals(entreprise.getUserId())) {
            throw new AccessDeniedException("Acces refuse a cette entreprise");
        }
    }
}
