package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.response.CompanyRegistryCompanyResponse;
import be.profacile.savefunds.api.dto.response.CompanyRegistryImportResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.service.company.CompanyRegistryImportService;
import be.profacile.savefunds.domain.service.company.CompanyRegistryProvider;
import be.profacile.savefunds.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-registry")
@RequiredArgsConstructor
@Tag(name = "BCE / Registre entreprises", description = "Recherche officielle d'entreprise avant creation dans SaveFunds")
public class CompanyRegistryController {

    private final CompanyRegistryProvider companyRegistryProvider;
    private final CompanyRegistryImportService companyRegistryImportService;
    private final CurrentUserService currentUserService;

    @GetMapping("/search")
    @Operation(summary = "Rechercher une entreprise dans le registre BCE")
    public ResponseEntity<List<CompanyRegistryCompanyResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(companyRegistryProvider.search(query));
    }

    @GetMapping("/{enterpriseNumber}")
    @Operation(summary = "Recuperer une entreprise par numero BCE")
    public ResponseEntity<CompanyRegistryCompanyResponse> findByEnterpriseNumber(@PathVariable String enterpriseNumber) {
        return ResponseEntity.ok(companyRegistryProvider.findByEnterpriseNumber(enterpriseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise BCE introuvable: " + enterpriseNumber)));
    }

    @PostMapping("/import")
    @Operation(summary = "Importer un extrait BCE Open Data CSV",
            description = "Charge un fichier CSV issu de BCE Open Data dans la base locale SaveFunds pour permettre la recherche gratuite.")
    public ResponseEntity<CompanyRegistryImportResponse> importCsv(@RequestParam("file") MultipartFile file) {
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.COMPTABLE) {
            throw new AccessDeniedException("Import BCE reserve aux administrateurs et comptables");
        }
        return ResponseEntity.ok(companyRegistryImportService.importCsv(file));
    }
}
