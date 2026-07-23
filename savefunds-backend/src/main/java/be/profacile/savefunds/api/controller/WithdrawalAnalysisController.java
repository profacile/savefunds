package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateWithdrawalAnalysisRequest;
import be.profacile.savefunds.api.dto.response.WithdrawalAnalysisResponse;
import be.profacile.savefunds.api.dto.response.AnalysisResultResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.WithdrawalAnalysisMapper;
import be.profacile.savefunds.api.mapper.AnalysisResultMapper;
import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;
import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.entity.AnalysisResult;
import be.profacile.savefunds.domain.enums.AnalysisStatus;

import be.profacile.savefunds.domain.service.CompanyService;
import be.profacile.savefunds.domain.service.AnalysisResultService;
import be.profacile.savefunds.security.service.CurrentUserService;
import be.profacile.savefunds.service.WithdrawalAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analyses")
@RequiredArgsConstructor
@Tag(name = "Analyses", description = "Gestion des analyses de prélèvement")
public class WithdrawalAnalysisController {

    private final WithdrawalAnalysisService withdrawalAnalysisService;
    private final CompanyService companyService;
    private final AnalysisResultService resultWithdrawalAnalysisService;
    private final WithdrawalAnalysisMapper withdrawalAnalysisMapper;
    private final AnalysisResultMapper resultWithdrawalAnalysisMapper;
    private final CurrentUserService currentUserService;

    @PostMapping
    @Operation(summary = "Créer une analysis",
            description = "Crée une nouvelle demande d'analysis de prélèvement pour une company")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Analyse créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Company non trouvée")
    })
    public ResponseEntity<WithdrawalAnalysisResponse> createAnalysis(
            @Valid @RequestBody CreateWithdrawalAnalysisRequest request) {

        assertOwnsCompany(request.getCompanyId());
        WithdrawalAnalysis analysis = withdrawalAnalysisService.create(
                request.getCompanyId(),
                request.getRequestedAmount()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(withdrawalAnalysisMapper.toResponse(analysis));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Lister les analyses d'une company",
            description = "Récupère toutes les analyses de prélèvement pour une company donnée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Company non trouvée")
    })
    public ResponseEntity<List<WithdrawalAnalysisResponse>> listCompanyAnalyses(
            @Parameter(description = "ID de l'company")
            @PathVariable Long companyId) {

        assertOwnsCompany(companyId);
        List<WithdrawalAnalysis> analyses = withdrawalAnalysisService.findByCompanyId(companyId);

        List<WithdrawalAnalysisResponse> responses = analyses.stream()
                .map(withdrawalAnalysisMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/result")
    @Operation(summary = "Lancer l'analysis",
            description = "Effectue le calcul des indicateurs et génère la décision tricolore")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analyse effectuée avec succès"),
            @ApiResponse(responseCode = "404", description = "Analyse non trouvée"),
            @ApiResponse(responseCode = "400", description = "Analyse déjà effectuée ou données incomplètes")
    })
    public ResponseEntity<AnalysisResultResponse> runAnalysisEndpoint(
            @Parameter(description = "ID de l'analysis")
            @PathVariable Long id) {

        assertOwnsAnalyse(id);
        AnalysisResult result = withdrawalAnalysisService.runAnalysis(id);

        return ResponseEntity.ok(resultWithdrawalAnalysisMapper.toResponse(result));
    }

    @GetMapping("/{id}/result")
    @Operation(summary = "Récupérer le résultat d'une analysis",
            description = "Récupère le résultat complet d'une analysis terminée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Résultat récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Analyse ou résultat non trouvé"),
            @ApiResponse(responseCode = "400", description = "Analyse pas encore effectuée")
    })
    public ResponseEntity<AnalysisResultResponse> getResult(
            @Parameter(description = "ID de l'analysis")
            @PathVariable Long id) {

        WithdrawalAnalysis analysis = withdrawalAnalysisService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée avec l'ID: " + id));
        assertOwnsAnalyse(analysis);

        if (analysis.getStatus() != AnalysisStatus.TERMINEE) {
            throw new IllegalStateException("L'analysis n'a pas encore été effectuée");
        }

        AnalysisResult result = resultWithdrawalAnalysisService.findByAnalysis_Id(id)
                .orElseThrow(() -> new ResourceNotFoundException("Résultat non trouvé pour l'analysis ID: " + id));

        return ResponseEntity.ok(resultWithdrawalAnalysisMapper.toResponse(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une analysis",
            description = "Récupère les détails d'une analysis par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analyse récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Analyse non trouvée")
    })
    public ResponseEntity<WithdrawalAnalysisResponse> getAnalysis(
            @Parameter(description = "ID de l'analysis")
            @PathVariable Long id) {

        WithdrawalAnalysis analysis = withdrawalAnalysisService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée avec l'ID: " + id));
        assertOwnsAnalyse(analysis);

        return ResponseEntity.ok(withdrawalAnalysisMapper.toResponse(analysis));
    }

    private void assertOwnsAnalyse(Long analysisId) {
        WithdrawalAnalysis analysis = withdrawalAnalysisService.findById(analysisId)
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée avec l'ID: " + analysisId));
        assertOwnsAnalyse(analysis);
    }

    private void assertOwnsAnalyse(WithdrawalAnalysis analysis) {
        assertCurrentUser(analysis.getCompany().getUserId());
    }

    private void assertOwnsCompany(Long companyId) {
        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company non trouvée avec l'ID: " + companyId));
        assertCurrentUser(company.getUserId());
    }

    private void assertCurrentUser(Long userId) {
        if (!currentUserService.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Acces refuse a cette analysis");
        }
    }
}
