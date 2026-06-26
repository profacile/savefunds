package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateAccountantNoteRequest;
import be.profacile.savefunds.api.dto.request.CreateValidationDecisionRequest;
import be.profacile.savefunds.api.dto.request.DecideValidationRequest;
import be.profacile.savefunds.api.dto.response.AccountantDashboardResponse;
import be.profacile.savefunds.api.dto.response.AccountantNoteResponse;
import be.profacile.savefunds.api.dto.response.ValidationDecisionResponse;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.service.AccountantDashboardService;
import be.profacile.savefunds.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accountants")
@RequiredArgsConstructor
@Tag(name = "Comptables", description = "Portefeuille clients, notes internes et validations comptables")
public class AccountantController {

    private final AccountantDashboardService accountantDashboardService;
    private final CurrentUserService currentUserService;

    @GetMapping("/me/dashboard")
    @Operation(summary = "Tableau de bord comptable",
            description = "Retourne les clients priorises par score de risque, echeance, fraicheur des donnees et validations en attente.")
    public ResponseEntity<AccountantDashboardResponse> dashboard() {
        User accountant = currentUserService.getCurrentUser();
        return ResponseEntity.ok(accountantDashboardService.dashboard(accountant));
    }

    @PostMapping("/entreprises/{entrepriseId}/notes")
    @Operation(summary = "Ajouter une note interne sur un dossier client")
    public ResponseEntity<AccountantNoteResponse> addNote(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody CreateAccountantNoteRequest request) {
        User accountant = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountantDashboardService.addNote(accountant, entrepriseId, request));
    }

    @PostMapping("/entreprises/{entrepriseId}/validation-requests")
    @Operation(summary = "Creer une demande de validation comptable",
            description = "Utilise par le dirigeant pour demander l'avis du comptable avant un retrait ou une depense.")
    public ResponseEntity<ValidationDecisionResponse> createValidationRequest(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody CreateValidationDecisionRequest request) {
        User requester = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountantDashboardService.createValidationRequest(requester, entrepriseId, request));
    }

    @PutMapping("/validation-requests/{validationId}/decision")
    @Operation(summary = "Valider, refuser, reporter ou valider sous condition une demande")
    public ResponseEntity<ValidationDecisionResponse> decide(
            @PathVariable Long validationId,
            @Valid @RequestBody DecideValidationRequest request) {
        User accountant = currentUserService.getCurrentUser();
        return ResponseEntity.ok(accountantDashboardService.decide(accountant, validationId, request));
    }
}
