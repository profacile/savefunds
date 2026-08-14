package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateAccountantNoteRequest;
import be.profacile.savefunds.api.dto.request.CreateValidationDecisionRequest;
import be.profacile.savefunds.api.dto.request.DecideAccountantClientAccessRequest;
import be.profacile.savefunds.api.dto.request.DecideValidationRequest;
import be.profacile.savefunds.api.dto.request.RequestAccountantClientAccessRequest;
import be.profacile.savefunds.api.dto.response.AccountantClientAccessResponse;
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

import java.util.List;

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

    @PostMapping("/client-access-requests")
    @Operation(summary = "Demander l'acces comptable a une entreprise",
            description = "Le comptable demande le rattachement a une entreprise deja creee dans SaveFunds. Le dirigeant doit accepter.")
    public ResponseEntity<AccountantClientAccessResponse> requestClientAccess(
            @Valid @RequestBody RequestAccountantClientAccessRequest request) {
        User accountant = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountantDashboardService.requestClientAccess(accountant, request));
    }

    @GetMapping("/client-access-requests")
    @Operation(summary = "Lister mes demandes de rattachement comptable")
    public ResponseEntity<List<AccountantClientAccessResponse>> myClientAccessRequests() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(accountantDashboardService.myClientAccessRequests(user));
    }

    @PutMapping("/client-access-requests/{accessId}/decision")
    @Operation(summary = "Accepter, refuser ou revoquer une demande d'acces comptable")
    public ResponseEntity<AccountantClientAccessResponse> decideClientAccess(
            @PathVariable Long accessId,
            @Valid @RequestBody DecideAccountantClientAccessRequest request) {
        User director = currentUserService.getCurrentUser();
        return ResponseEntity.ok(accountantDashboardService.decideClientAccess(director, accessId, request));
    }

    @PostMapping("/companies/{companyId}/notes")
    @Operation(summary = "Ajouter une note interne sur un dossier client")
    public ResponseEntity<AccountantNoteResponse> addNote(
            @PathVariable Long companyId,
            @Valid @RequestBody CreateAccountantNoteRequest request) {
        User accountant = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountantDashboardService.addNote(accountant, companyId, request));
    }

    @PostMapping("/companies/{companyId}/validation-requests")
    @Operation(summary = "Creer une demande de validation comptable",
            description = "Utilise par le dirigeant pour demander l'avis du comptable avant un retrait ou une depense.")
    public ResponseEntity<ValidationDecisionResponse> createValidationRequest(
            @PathVariable Long companyId,
            @Valid @RequestBody CreateValidationDecisionRequest request) {
        User requester = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountantDashboardService.createValidationRequest(requester, companyId, request));
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
