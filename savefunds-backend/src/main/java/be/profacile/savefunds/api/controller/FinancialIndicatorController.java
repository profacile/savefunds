package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.service.CompanyService;
import be.profacile.savefunds.domain.service.FinancialIndicatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Controller pour les calculs d'indicateurs financiers.
 *
 * Deux types d'endpoints :
 * 1. Calculs unitaires (valeurs brutes passées en @RequestParam) — utiles pour le frontend
 *    afin de simuler sans créer d'analysis
 * 2. Calculs sur une company existante (via companyId) — sécurisé, pas d'entité exposée
 */
@RestController
@RequestMapping("/api/v1/indicateurs")
@RequiredArgsConstructor
@Tag(name = "FinancialIndicators", description = "Calculs d'indicateurs financiers")
public class FinancialIndicatorController {

    private final FinancialIndicatorService financialIndicatorService;
    private final CompanyService companyService;

    /**
     * Calcul unitaire du ratio CA/Charges.
     * Utile pour simulation frontend sans passer par une analysis.
     */
    @GetMapping("/ratio-ca-charges")
    @Operation(summary = "Calculer le ratio CA/Charges",
            description = "Calcule le ratio chiffre d'affaires / charges à partir de valeurs brutes")
    public ResponseEntity<BigDecimal> calculateRevenueExpensesRatio(
            @Parameter(description = "Chiffre d'affaires mensuel", example = "50000")
            @RequestParam BigDecimal chiffreAffaires,
            @Parameter(description = "Charges mensuelles", example = "30000")
            @RequestParam BigDecimal charges) {

        return ResponseEntity.ok(
                financialIndicatorService.calculateRevenueExpensesRatio(chiffreAffaires, charges));
    }

    /**
     * Calcul unitaire de la trésorerie en mois.
     */
    @GetMapping("/cashBalance-en-mois")
    @Operation(summary = "Calculer la trésorerie en mois",
            description = "Calcule le lastNamebre de mois de charges couverts par la trésorerie")
    public ResponseEntity<BigDecimal> calculateCashCoverageMonths(
            @Parameter(description = "Trésorerie disponible", example = "100000")
            @RequestParam BigDecimal cashBalance,
            @Parameter(description = "Charges mensuelles", example = "30000")
            @RequestParam BigDecimal monthlyExpenses) {

        return ResponseEntity.ok(
                financialIndicatorService.calculateCashCoverageMonths(cashBalance, monthlyExpenses));
    }

    /**
     * Calcul unitaire de la durée du compte courant débiteur.
     */
    @GetMapping("/duree-compte-courant-debiteur")
    @Operation(summary = "Calculer la durée du compte courant débiteur",
            description = "Calcule le lastNamebre de jours consécutifs en compte courant débiteur")
    public ResponseEntity<Integer> calculateDirectorCurrentAccountDebtorDays(
            @Parameter(description = "Solde du compte courant", example = "-5000")
            @RequestParam BigDecimal directorCurrentAccountBalance,
            @Parameter(description = "Date de début du débit (optionnel)", example = "2026-03-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutDebiteur) {

        return ResponseEntity.ok(
                financialIndicatorService.calculateDirectorCurrentAccountDebtorDays(
                        directorCurrentAccountBalance, dateDebutDebiteur));
    }

    /**
     * Calcul du montant maximum prélevable.
     */
    @GetMapping("/montant-maximum-prelevable")
    @Operation(summary = "Calculer le montant maximum prélevable",
            description = "Calcule le montant maximum qui peut être prélevé en respectant le seuil de sécurité")
    public ResponseEntity<BigDecimal> calculateMaximumWithdrawableAmount(
            @Parameter(description = "Trésorerie disponible", example = "100000")
            @RequestParam BigDecimal cashBalance,
            @Parameter(description = "Charges mensuelles", example = "30000")
            @RequestParam BigDecimal monthlyExpenses,
            @Parameter(description = "Seuil de sécurité en mois", example = "3")
            @RequestParam(defaultValue = "3") int seuilSecurite) {

        return ResponseEntity.ok(
                financialIndicatorService.calculateMaximumWithdrawableAmount(
                        cashBalance, monthlyExpenses, seuilSecurite));
    }

    /**
     * Calcul de TOUS les indicateurs pour une company existante.
     * Utilise companyId — jamais l'entité directement.
     */
    @GetMapping("/company/{companyId}/tous")
    @Operation(summary = "Calculer tous les indicateurs d'une company",
            description = "Calcule l'ensemble des indicateurs financiers pour une company existante")
    public ResponseEntity<Map<String, Object>> calculateAllFinancialIndicators(
            @Parameter(description = "ID de l'company")
            @PathVariable Long companyId) {

        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company non trouvée avec l'ID: " + companyId));

        return ResponseEntity.ok(financialIndicatorService.calculateAllFinancialIndicators(company));
    }

    /**
     * Vérifie si les données financières d'une company sont complètes.
     */
    @GetMapping("/company/{companyId}/donnees-completes")
    @Operation(summary = "Vérifier la complétude des données",
            description = "Vérifie si toutes les données financières nécessaires sont renseignées")
    public ResponseEntity<Boolean> hasCompleteFinancialData(
            @Parameter(description = "ID de l'company")
            @PathVariable Long companyId) {

        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company non trouvée avec l'ID: " + companyId));

        return ResponseEntity.ok(financialIndicatorService.hasCompleteFinancialData(company));
    }
}