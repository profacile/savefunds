package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.ChangePasswordRequest;
import be.profacile.savefunds.api.dto.request.UpdateUserRequest;
import be.profacile.savefunds.api.dto.response.UserResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.UserMapper;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller de gestion des utilisateurs.
 *
 * Note : La création d'utilisateur est gérée par AuthController (/api/auth/register).
 * Ce controller gère le profil et l'administration des comptes existants.
 *
 * Endpoints:
 * - GET  /api/v1/users          : Lister tous les utilisateurs (ADMIN)
 * - GET  /api/v1/users/{id}     : Récupérer un utilisateur
 * - PUT  /api/v1/users/{id}     : Mettre à jour le profil
 * - DELETE /api/v1/users/{id}   : Supprimer un compte
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des comptes utilisateurs")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Lister tous les utilisateurs.
     * Usage réservé à l'administration.
     */
    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs",
            description = "Récupère la liste complète des utilisateurs (usage admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<List<UserResponse>> listerUtilisateurs() {
        List<UserResponse> responses = userService.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Récupérer un utilisateur par son ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur",
            description = "Récupère les détails d'un utilisateur par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<UserResponse> recupererUtilisateur(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long id) {

        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé avec l'ID: " + id));

        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    /**
     * Récupérer un utilisateur par son email.
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Récupérer un utilisateur par email",
            description = "Récupère un utilisateur via son adresse email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<UserResponse> recupererParEmail(
            @Parameter(description = "Email de l'utilisateur")
            @PathVariable String email) {

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé avec l'email: " + email));

        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    /**
     * Mettre à jour le profil d'un utilisateur.
     * L'email n'est pas modifiable.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur",
            description = "Modifie les informations d'un utilisateur (nom, prénom, rôle). L'email n'est pas modifiable.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<UserResponse> mettreAJourUtilisateur(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        User existing = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé avec l'ID: " + id));

        // Appliquer les modifications via le mapper (champs protégés ignorés)
        userMapper.updateFromRequest(request, existing);

        User updated = userService.update(id, existing);

        return ResponseEntity.ok(userMapper.toResponse(updated));
    }

    /**
     * Supprimer un compte utilisateur.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur",
            description = "Supprime définitivement un compte utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> supprimerUtilisateur(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long id) {

        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Changer le mot de passe")
    public ResponseEntity<Void> changerMotDePasse(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getAncienMotDePasse(), request.getNouveauMotDePasse());
        return ResponseEntity.noContent().build();
    }
}