package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateEntrepriseRequest;
import be.profacile.savefunds.api.dto.request.UpdateEntrepriseRequest;
import be.profacile.savefunds.api.dto.response.EntrepriseResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.EntrepriseMapper;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.service.EntrepriseService;
import be.profacile.savefunds.security.service.CurrentUserService;
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

/**
 * REST Controller pour la gestion des entreprises (PME/SRL belges).
 *
 * Règle métier : 1 userId = 1 entreprise maximum
 *
 * Endpoints:
 * - POST /api/v1/entreprises : Créer une entreprise
 * - GET /api/v1/entreprises/{id} : Récupérer par ID
 * - GET /api/v1/entreprises/user/{userId} : Récupérer par userId
 * - PUT /api/v1/entreprises/{id} : Mettre à jour
 * - DELETE /api/v1/entreprises/{id} : Supprimer
 */
@RestController
@RequestMapping("/api/v1/entreprises")
@RequiredArgsConstructor
@Tag(name = "Entreprises", description = "Gestion des entreprises (PME/SRL)")
public class EntrepriseController {

    private final EntrepriseService entrepriseService;
    private final EntrepriseMapper entrepriseMapper;
    private final CurrentUserService currentUserService;

    /**
     * Créer une nouvelle entreprise.
     * Règle : 1 userId = 1 entreprise max
     */
    @PostMapping
    @Operation(summary = "Créer une entreprise",
            description = "Enregistre une nouvelle entreprise pour un utilisateur (1 entreprise max par user)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entreprise créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur a déjà une entreprise"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<EntrepriseResponse> creerEntreprise(
            @Valid @RequestBody CreateEntrepriseRequest request) {

        Entreprise entreprise = entrepriseMapper.toEntity(request);
        entreprise.setUserId(currentUserService.getCurrentUserId());
        Entreprise created = entrepriseService.create(entreprise);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entrepriseMapper.toResponse(created));
    }

    /**
     * Récupérer une entreprise par son ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une entreprise par ID",
            description = "Récupère les détails d'une entreprise via son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entreprise récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    })
    public ResponseEntity<EntrepriseResponse> recupererEntreprise(
            @Parameter(description = "ID de l'entreprise")
            @PathVariable Long id) {

        Entreprise entreprise = entrepriseService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'ID: " + id));
        assertOwnsEntreprise(entreprise);

        return ResponseEntity.ok(entrepriseMapper.toResponse(entreprise));
    }

    /**
     * Récupérer l'entreprise d'un utilisateur.
     * Utile car 1 user = 1 entreprise max
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer l'entreprise d'un utilisateur",
            description = "Récupère l'entreprise associée à un utilisateur (1 max par user)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entreprise récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune entreprise trouvée pour cet utilisateur")
    })
    public ResponseEntity<EntrepriseResponse> recupererEntrepriseParUser(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long userId) {

        assertCurrentUser(userId);
        Entreprise entreprise = entrepriseService.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune entreprise trouvée pour l'utilisateur ID: " + userId));

        return ResponseEntity.ok(entrepriseMapper.toResponse(entreprise));
    }

    /**
     * Mettre à jour une entreprise existante.
     * Mise à jour partielle (field-by-field)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une entreprise",
            description = "Modifie les données d'une entreprise existante (mise à jour partielle)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entreprise mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    })
    public ResponseEntity<EntrepriseResponse> mettreAJourEntreprise(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEntrepriseRequest request) {

        Entreprise updates = entrepriseMapper.toEntity(request);
        Entreprise existing = entrepriseService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'ID: " + id));
        assertOwnsEntreprise(existing);

        // Le service gère lui-même le 404 si l'entreprise n'existe pas
        Entreprise updated = entrepriseService.update(id, updates);

        return ResponseEntity.ok(entrepriseMapper.toResponse(updated));
    }

    /**
     * Supprimer une entreprise.
     * ATTENTION : Supprime aussi les analyses et situations financières associées (cascade)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une entreprise",
            description = "Supprime une entreprise et toutes ses données associées (analyses, situations financières)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entreprise supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    })
    public ResponseEntity<Void> supprimerEntreprise(
            @Parameter(description = "ID de l'entreprise")
            @PathVariable Long id) {

        // Vérifier que l'entreprise existe
        Entreprise entreprise = entrepriseService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'ID: " + id));
        assertOwnsEntreprise(entreprise);

        entrepriseService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * BONUS : Vérifier si un utilisateur a déjà une entreprise.
     * Utile pour le frontend avant d'afficher le formulaire de création
     */
    @GetMapping("/user/{userId}/exists")
    @Operation(summary = "Vérifier si l'utilisateur a une entreprise",
            description = "Vérifie si un utilisateur a déjà enregistré une entreprise")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vérification effectuée")
    })
    public ResponseEntity<Boolean> verifierEntrepriseExistante(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long userId) {

        assertCurrentUser(userId);
        boolean exists = entrepriseService.findByUserId(userId).isPresent();
        return ResponseEntity.ok(exists);
    }

    private void assertOwnsEntreprise(Entreprise entreprise) {
        assertCurrentUser(entreprise.getUserId());
    }

    private void assertCurrentUser(Long userId) {
        if (!currentUserService.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Acces refuse a cette entreprise");
        }
    }
}
