package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.ChangePasswordRequest;
import be.profacile.savefunds.api.dto.request.UpdateUserRequest;
import be.profacile.savefunds.api.dto.response.UserResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.UserMapper;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.service.UserService;
import be.profacile.savefunds.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des comptes utilisateurs")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    @Operation(summary = "Recuperer le profil connecte",
            description = "Retourne l'utilisateur associe au token JWT courant")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userMapper.toResponse(currentUserService.getCurrentUser()));
    }

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs",
            description = "Reserve aux administrateurs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste recuperee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces reserve aux administrateurs")
    })
    public ResponseEntity<List<UserResponse>> listerUtilisateurs() {
        assertAdmin();
        List<UserResponse> responses = userService.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un utilisateur",
            description = "Un utilisateur peut consulter son profil. Un admin peut consulter tous les profils.")
    public ResponseEntity<UserResponse> recupererUtilisateur(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long id) {

        assertSelfOrAdmin(id);
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve avec l'ID: " + id));

        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Recuperer un utilisateur par email",
            description = "Un utilisateur peut consulter son profil. Un admin peut consulter tous les profils.")
    public ResponseEntity<UserResponse> recupererParEmail(
            @Parameter(description = "Email de l'utilisateur")
            @PathVariable String email) {

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve avec l'email: " + email));
        assertSelfOrAdmin(user.getId());

        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour un utilisateur",
            description = "Un utilisateur peut modifier son nom/prenom. Seul un admin peut modifier le role ou la verification email.")
    public ResponseEntity<UserResponse> mettreAJourUtilisateur(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        assertSelfOrAdmin(id);
        if (!isAdmin() && (request.getRole() != null || request.getEmailVerified() != null)) {
            throw new AccessDeniedException("Seul un administrateur peut modifier le role ou la verification email");
        }

        User existing = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve avec l'ID: " + id));
        userMapper.updateFromRequest(request, existing);

        User updated = userService.update(id, existing);

        return ResponseEntity.ok(userMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur",
            description = "Reserve aux administrateurs")
    public ResponseEntity<Void> supprimerUtilisateur(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long id) {

        assertAdmin();
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Changer le mot de passe",
            description = "Un utilisateur peut changer son propre mot de passe. Un admin peut aussi declencher l'operation.")
    public ResponseEntity<Void> changerMotDePasse(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        assertSelfOrAdmin(id);
        userService.changePassword(id, request.getAncienMotDePasse(), request.getNouveauMotDePasse());
        return ResponseEntity.noContent().build();
    }

    private void assertSelfOrAdmin(Long userId) {
        User current = currentUserService.getCurrentUser();
        if (current.getRole() == Role.ADMIN || current.getId().equals(userId)) {
            return;
        }
        throw new AccessDeniedException("Acces refuse a cet utilisateur");
    }

    private void assertAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Acces reserve aux administrateurs");
        }
    }

    private boolean isAdmin() {
        return currentUserService.getCurrentUser().getRole() == Role.ADMIN;
    }
}
