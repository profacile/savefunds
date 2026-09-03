package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateCompanyRequest;
import be.profacile.savefunds.api.dto.request.CreateCompanyFromRegistryRequest;
import be.profacile.savefunds.api.dto.request.UpdateCompanyRequest;
import be.profacile.savefunds.api.dto.response.CompanyRegistryCompanyResponse;
import be.profacile.savefunds.api.dto.response.CompanyResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.api.mapper.CompanyMapper;

import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.service.BnbAnnualAccountsService;
import be.profacile.savefunds.domain.service.CompanyService;
import be.profacile.savefunds.domain.service.company.CompanyRegistryProvider;
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

import java.util.List;
import java.util.Optional;

/**
 * REST Controller pour la gestion des companies (PME/SRL belges).
 *
 * Règle métier : 1 userId = 1 company maximum
 *
 * Endpoints:
 * - POST /api/v1/companies : Créer une company
 * - GET /api/v1/companies/{id} : Récupérer par ID
 * - GET /api/v1/companies/user/{userId} : Récupérer par userId
 * - PUT /api/v1/companies/{id} : Mettre à jour
 * - DELETE /api/v1/companies/{id} : Supprimer
 */
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Gestion des companies (PME/SRL)")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final CurrentUserService currentUserService;
    private final CompanyRegistryProvider companyRegistryProvider;
    private final BnbAnnualAccountsService bnbAnnualAccountsService;

    /**
     * Créer une nouvelle company.
     * Règle : 1 userId = 1 company max
     */
    @PostMapping
    @Operation(summary = "Créer une company",
            description = "Enregistre une nouvelle company pour un utilisateur (1 company max par user)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur a déjà une company"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CreateCompanyRequest request) {

        Company company = companyMapper.toEntity(request);
        company.setUserId(currentUserService.getCurrentUserId());
        Company created = companyService.create(company);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(companyMapper.toResponse(created));
    }

    @PostMapping("/from-registry")
    @Operation(summary = "Creer une company depuis la BCE",
            description = "Le dirigeant confirme une company issue du registre BCE. Les informations legales sont pre-remplies automatiquement.")
    public ResponseEntity<CompanyResponse> createFromRegistry(
            @Valid @RequestBody CreateCompanyFromRegistryRequest request) {

        Optional<CompanyRegistryCompanyResponse> registryCompany = companyRegistryProvider
                .findByEnterpriseNumber(request.getEnterpriseNumber());

        if (!Boolean.TRUE.equals(request.getOwnershipDeclarationAccepted())) {
            throw new IllegalArgumentException("Confirmez que vous etes dirigeant ou mandate pour rattacher cette entreprise.");
        }

        if (registryCompany.isEmpty()) {
            throw new IllegalArgumentException("Entreprise introuvable dans la BCE: " + request.getEnterpriseNumber());
        }

        if (!canAttachRegistryCompany(registryCompany.get())) {
            throw new IllegalArgumentException("Entreprise inactive selon la BCE: " + request.getEnterpriseNumber());
        }

        Company company = new Company();
        company.setUserId(currentUserService.getCurrentUserId());
        String companyName = firstUsable(
                request.getName(),
                registryCompany.map(CompanyRegistryCompanyResponse::getName).orElse(null),
                "Company BCE");
        company.setLegalName(companyName);
        company.setName(companyName);
        company.setEnterpriseNumber(firstUsable(
                normalizeEnterpriseNumber(request.getEnterpriseNumber()),
                registryCompany.map(CompanyRegistryCompanyResponse::getEnterpriseNumber).orElse(null),
                request.getEnterpriseNumber()));
        company.setLegalForm(firstUsable(
                request.getLegalForm(),
                registryCompany.map(CompanyRegistryCompanyResponse::getLegalForm).orElse(null),
                ""));
        company.setActivitySector(firstUsable(
                request.getActivityLabel(),
                registryCompany.map(CompanyRegistryCompanyResponse::getActivityLabel).orElse(null),
                firstUsable(request.getNaceCode(), registryCompany.map(CompanyRegistryCompanyResponse::getNaceCode).orElse(null), "")));

        Company created = companyService.create(company);
        bnbAnnualAccountsService.search(created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(companyMapper.toResponse(created));
    }

    private boolean canAttachRegistryCompany(CompanyRegistryCompanyResponse registryCompany) {
        if (registryCompany.isActive()) {
            return true;
        }

        String status = Optional.ofNullable(registryCompany.getStatus()).orElse("").toLowerCase();
        return !status.contains("inactif")
                && !status.contains("inactief")
                && !status.contains("radi")
                && !status.contains("stopgezet")
                && !status.contains("cess")
                && !status.contains("supprim");
    }

    /**
     * Récupérer une company par son ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une company par ID",
            description = "Récupère les détails d'une company via son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Company non trouvée")
    })
    public ResponseEntity<CompanyResponse> getCompany(
            @Parameter(description = "ID de l'company")
            @PathVariable Long id) {

        Company company = companyService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company non trouvée avec l'ID: " + id));
        assertOwnsCompany(company);

        return ResponseEntity.ok(companyMapper.toResponse(company));
    }

    @GetMapping("/me")
    @Operation(summary = "Lister mes companies",
            description = "Retourne toutes les companies rattachees au dirigeant connecte")
    public ResponseEntity<List<CompanyResponse>> listMyCompanies() {
        Long userId = currentUserService.getCurrentUserId();
        List<CompanyResponse> companies = companyService.findAllByUserId(userId).stream()
                .map(companyMapper::toResponse)
                .toList();

        return ResponseEntity.ok(companies);
    }

    /**
     * Récupérer l'company d'un utilisateur.
     * Utile car 1 user = 1 company max
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer l'company d'un utilisateur",
            description = "Récupère l'company associée à un utilisateur (1 max par user)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune company trouvée pour cet utilisateur")
    })
    public ResponseEntity<CompanyResponse> getCompanyByUser(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long userId) {

        assertCurrentUser(userId);
        Company company = companyService.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune company trouvée pour l'utilisateur ID: " + userId));

        return ResponseEntity.ok(companyMapper.toResponse(company));
    }

    /**
     * Mettre à jour une company existante.
     * Mise à jour partielle (field-by-field)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une company",
            description = "Modifie les données d'une company existante (mise à jour partielle)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Company non trouvée")
    })
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCompanyRequest request) {

        Company updates = companyMapper.toEntity(request);
        Company existing = companyService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company non trouvée avec l'ID: " + id));
        assertOwnsCompany(existing);

        // Le service gère lui-même le 404 si l'company n'existe pas
        Company updated = companyService.update(id, updates);

        return ResponseEntity.ok(companyMapper.toResponse(updated));
    }

    /**
     * Supprimer une company.
     * ATTENTION : Supprime aussi les analyses et situations financières associées (cascade)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une company",
            description = "Supprime une company et toutes ses données associées (analyses, situations financières)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Company non trouvée")
    })
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "ID de l'company")
            @PathVariable Long id) {

        // Vérifier que l'company existe
        Company company = companyService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company non trouvée avec l'ID: " + id));
        assertOwnsCompany(company);

        companyService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * BONUS : Vérifier si un utilisateur a déjà une company.
     * Utile pour le frontend avant d'afficher le formulaire de création
     */
    @GetMapping("/user/{userId}/exists")
    @Operation(summary = "Vérifier si l'utilisateur a une company",
            description = "Vérifie si un utilisateur a déjà enregistré une company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vérification effectuée")
    })
    public ResponseEntity<Boolean> verifierCompanyExistante(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Long userId) {

        assertCurrentUser(userId);
        boolean exists = !companyService.findAllByUserId(userId).isEmpty();
        return ResponseEntity.ok(exists);
    }

    private void assertOwnsCompany(Company company) {
        assertCurrentUser(company.getUserId());
    }

    private void assertCurrentUser(Long userId) {
        if (!currentUserService.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Acces refuse a cette company");
        }
    }

    private String firstUsable(String primary, String secondary, String fallback) {
        if (isUsableCompanyText(primary)) {
            return primary.trim();
        }
        if (isUsableCompanyText(secondary)) {
            return secondary.trim();
        }
        return fallback;
    }

    private String normalizeEnterpriseNumber(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.length() != 10) {
            return value.trim();
        }
        return "BE " + digits.substring(0, 4) + "." + digits.substring(4, 7) + "." + digits.substring(7);
    }

    private boolean isUsableCompanyText(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toLowerCase();
        return !normalized.startsWith("ondernemingsnummer")
                && !normalized.startsWith("numero d")
                && !normalized.startsWith("numéro d");
    }
}
