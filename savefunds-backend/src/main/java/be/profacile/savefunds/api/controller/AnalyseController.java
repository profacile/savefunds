package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateAnalyseRequest;
import be.profacile.savefunds.api.dto.response.AnalyseResponse;
import be.profacile.savefunds.api.dto.response.ResultatAnalyseResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.AnalyseMapper;
import be.profacile.savefunds.api.mapper.ResultatAnalyseMapper;
import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import be.profacile.savefunds.domain.enums.StatutAnalyse;

import be.profacile.savefunds.domain.service.EntrepriseService;
import be.profacile.savefunds.domain.service.ResultatAnalyseService;
import be.profacile.savefunds.security.service.CurrentUserService;
import be.profacile.savefunds.service.AnalyseService;
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
public class AnalyseController {

    private final AnalyseService analyseService;
    private final EntrepriseService entrepriseService;
    private final ResultatAnalyseService resultatAnalyseService;
    private final AnalyseMapper analyseMapper;
    private final ResultatAnalyseMapper resultatAnalyseMapper;
    private final CurrentUserService currentUserService;

    @PostMapping
    @Operation(summary = "Créer une analyse",
            description = "Crée une nouvelle demande d'analyse de prélèvement pour une entreprise")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Analyse créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    })
    public ResponseEntity<AnalyseResponse> creerAnalyse(
            @Valid @RequestBody CreateAnalyseRequest request) {

        assertOwnsEntreprise(request.getEntrepriseId());
        AnalysePrelevement analyse = analyseService.create(
                request.getEntrepriseId(),
                request.getMontantSouhaite()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(analyseMapper.toResponse(analyse));
    }

    @GetMapping("/entreprise/{entrepriseId}")
    @Operation(summary = "Lister les analyses d'une entreprise",
            description = "Récupère toutes les analyses de prélèvement pour une entreprise donnée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    })
    public ResponseEntity<List<AnalyseResponse>> listerAnalysesEntreprise(
            @Parameter(description = "ID de l'entreprise")
            @PathVariable Long entrepriseId) {

        assertOwnsEntreprise(entrepriseId);
        List<AnalysePrelevement> analyses = analyseService.findByEntrepriseId(entrepriseId);

        List<AnalyseResponse> responses = analyses.stream()
                .map(analyseMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/analyser")
    @Operation(summary = "Lancer l'analyse",
            description = "Effectue le calcul des indicateurs et génère la décision tricolore")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analyse effectuée avec succès"),
            @ApiResponse(responseCode = "404", description = "Analyse non trouvée"),
            @ApiResponse(responseCode = "400", description = "Analyse déjà effectuée ou données incomplètes")
    })
    public ResponseEntity<ResultatAnalyseResponse> lancerAnalyse(
            @Parameter(description = "ID de l'analyse")
            @PathVariable Long id) {

        assertOwnsAnalyse(id);
        ResultatAnalyse resultat = analyseService.effectuerAnalyse(id);

        return ResponseEntity.ok(resultatAnalyseMapper.toResponse(resultat));
    }

    @GetMapping("/{id}/resultat")
    @Operation(summary = "Récupérer le résultat d'une analyse",
            description = "Récupère le résultat complet d'une analyse terminée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Résultat récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Analyse ou résultat non trouvé"),
            @ApiResponse(responseCode = "400", description = "Analyse pas encore effectuée")
    })
    public ResponseEntity<ResultatAnalyseResponse> recupererResultat(
            @Parameter(description = "ID de l'analyse")
            @PathVariable Long id) {

        AnalysePrelevement analyse = analyseService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée avec l'ID: " + id));
        assertOwnsAnalyse(analyse);

        if (analyse.getStatut() != StatutAnalyse.TERMINEE) {
            throw new IllegalStateException("L'analyse n'a pas encore été effectuée");
        }

        ResultatAnalyse resultat = resultatAnalyseService.findByAnalyse_Id(id)
                .orElseThrow(() -> new ResourceNotFoundException("Résultat non trouvé pour l'analyse ID: " + id));

        return ResponseEntity.ok(resultatAnalyseMapper.toResponse(resultat));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une analyse",
            description = "Récupère les détails d'une analyse par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analyse récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Analyse non trouvée")
    })
    public ResponseEntity<AnalyseResponse> recupererAnalyse(
            @Parameter(description = "ID de l'analyse")
            @PathVariable Long id) {

        AnalysePrelevement analyse = analyseService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée avec l'ID: " + id));
        assertOwnsAnalyse(analyse);

        return ResponseEntity.ok(analyseMapper.toResponse(analyse));
    }

    private void assertOwnsAnalyse(Long analyseId) {
        AnalysePrelevement analyse = analyseService.findById(analyseId)
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée avec l'ID: " + analyseId));
        assertOwnsAnalyse(analyse);
    }

    private void assertOwnsAnalyse(AnalysePrelevement analyse) {
        assertCurrentUser(analyse.getEntreprise().getUserId());
    }

    private void assertOwnsEntreprise(Long entrepriseId) {
        Entreprise entreprise = entrepriseService.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'ID: " + entrepriseId));
        assertCurrentUser(entreprise.getUserId());
    }

    private void assertCurrentUser(Long userId) {
        if (!currentUserService.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Acces refuse a cette analyse");
        }
    }
}
