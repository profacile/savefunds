package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.ForgotPasswordRequest;
import be.profacile.savefunds.api.dto.request.LoginRequest;
import be.profacile.savefunds.api.dto.request.RegisterRequest;
import be.profacile.savefunds.api.dto.response.AuthResponse;
import be.profacile.savefunds.api.dto.response.MessageResponse;
import be.profacile.savefunds.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription, connexion et recuperation du token JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Creer un compte utilisateur",
            description = "Inscrit un nouvel utilisateur et retourne un token JWT utilisable dans Swagger via le bouton Authorize."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte cree et token JWT genere"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou email deja utilise")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Se connecter",
            description = "Authentifie un utilisateur et retourne un token JWT. Copier le token dans Authorize sous la forme: Bearer <token>."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification reussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Demander une reinitialisation de mot de passe",
            description = "MVP: retourne un message neutre. L'envoi email reel sera branche dans une evolution production."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande prise en compte")
    })
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.requestPasswordReset(request.getEmail()));
    }
}
